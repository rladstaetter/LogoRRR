package app.logorrr.views.text

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundId, DataModelEvent, LogEntry, ScrollToActiveLogEntry}
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.util.Subscription
import net.ladstatt.util.log.TinyLog

import java.util
import java.util.function.Predicate
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.compiletime.uninitialized

object LogTextView extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogTextView])


class LogTextView(filteredList: FilteredList[LogEntry])
  extends ListView[LogEntry]
    with TinyLog with BoundId(LogTextView.uiNode(_).value):

  private val sharedSelectedSelection = new SimpleSetProperty[Int]()

  private lazy val selectedListener: ListChangeListener[LogEntry] = new ListChangeListener[LogEntry] {
    override def onChanged(c: ListChangeListener.Change[? <: LogEntry]): Unit =
      println(getSelectionModel.getSelectedItems.asScala.foreach(println))
      val selectedLines = new util.HashSet[Int]()
      val selected = getSelectionModel.getSelectedItems.forEach(entry => selectedLines.add(entry.lineNumber))
      sharedSelectedSelection.retainAll(selectedLines)
      sharedSelectedSelection.addAll(selectedLines)
  }


  private val fontsizeProperty = new SimpleIntegerProperty()
  private val fontsizeSubscription: Subscription = fontsizeProperty.subscribe(_ => refresh())
  private val firstVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  private val lastVisibleTextCellIndexProperty = new SimpleIntegerProperty()
  private var someScrollBarSubscription: Option[Subscription] = None

  var searchTermChangeListener: MutableSearchTermListener = uninitialized

  /** recalculated if searchterms change */
  val searchTermsAndColors: ObservableList[(String, Color)] = FXCollections.observableArrayList[(String, Color)]()

  /**
   * to observe the visible text and mark it in the boxview
   */
  private val skinSubscriber = skinProperty.subscribe(_ => {
    someScrollBarSubscription =
      ListViewHelper.findScrollBar(this).map(scrollBar => scrollBar.valueProperty.subscribe(_ => {
        val (first, last) = ListViewHelper.getVisibleRange(this)
        firstVisibleTextCellIndexProperty.set(first)
        lastVisibleTextCellIndexProperty.set(last)
      }))
  })

  /** contains number of digits of max size of filtered list */
  val maxSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty() {
    this.bind(Bindings.createIntegerBinding(() => filteredList.size.toString.length, filteredList))
  }

  def scrollToItem(item: LogEntry): Unit =
    val relativeIndex = getItems.indexOf(item)
    getSelectionModel.select(relativeIndex)
    val cellHeight = fontsizeProperty.get()
    JfxUtils.scrollTo[LogEntry](this, cellHeight, relativeIndex)

  def scrollToActiveLogEntry(): Unit =
    if getHeight != 0 then
      val candidates = filteredList.filtered(l => sharedSelectedSelection.contains(l.lineNumber))
      if !candidates.isEmpty then
        Option(candidates.get(0)) match
          case Some(selectedEntry) =>
            scrollToItem(selectedEntry)
            // to trigger ChunkListView scrollTo and repaint
            sharedSelectedSelection.add(selectedEntry.lineNumber)
          case None => // do nothing
    else
      logTrace("height was 0")


  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , sharedSelectedSelection: SetPropertyBase[Int]
           , fontsizeProperty: Property[Number]
           , firstVisibleTextCellIndexProperty: Property[Number]
           , lastVisibleTextCellIndexProperty: Property[Number]
           , mutSearchTerms: ObservableList[MutableSearchTerm]
          ): Unit =

    fixedCellSizeProperty().bind(Bindings.createDoubleBinding(
      () => fontsizeProperty.getValue.doubleValue() * 1.2,
      fontsizeProperty
    ))
    bindIdProperty(fileIdProperty)
    val activeSearchTerms = new FilteredList[MutableSearchTerm](mutSearchTerms, _.isActive)
    activeSearchTerms.forEach(st => searchTermsAndColors.add((st.getValue, st.getColor)))
    this.searchTermChangeListener = new MutableSearchTermListener(activeSearchTerms, searchTermsAndColors, this)
    mutSearchTerms.addListener(searchTermChangeListener)
    this.sharedSelectedSelection.bindBidirectional(sharedSelectedSelection)
    this.fontsizeProperty.bind(fontsizeProperty)
    this.firstVisibleTextCellIndexProperty.bindBidirectional(firstVisibleTextCellIndexProperty)
    this.lastVisibleTextCellIndexProperty.bindBidirectional(lastVisibleTextCellIndexProperty)
    setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell(this, filteredList, this.searchTermsAndColors, this.sharedSelectedSelection, scrollToActiveLogEntry, this.fontsizeProperty, this.maxSizeProperty))
    setItems(filteredList)
    getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    val selectedIndices: Array[Int] = sharedSelectedSelection.stream.mapToInt(e => e).toArray
    if (selectedIndices.length > 0) {
      getSelectionModel.selectIndices(selectedIndices(0), selectedIndices *)
    }
    // init selected listener only after having set initial values
    getSelectionModel.getSelectedItems.addListener(selectedListener)


    // hack to delay calling scrollToActiveLogEntry only after listview has a nonzero height
    val heightListener = new ChangeListener[Number] {
      override def changed(obs: ObservableValue[? <: Number], oldVal: Number, newVal: Number): Unit = {
        if (newVal.doubleValue() > 0) {
          scrollToActiveLogEntry()
          // Unregister self so we don't scroll every time the window is resized
          heightProperty().removeListener(this)
        }
      }
    }
    heightProperty.addListener(heightListener)

  /** clean up listeners */
  def shutdown(sharedElementCollection: SetPropertyBase[Int]
               , firstVisibleTextCellIndexProperty: Property[Number]
               , lastVisibleTextCellIndexProperty: Property[Number]
               , mutableSearchTerms: ObservableList[MutableSearchTerm]
              ): Unit =
    unbindIdProperty()
    fixedCellSizeProperty().unbind()
    mutableSearchTerms.removeListener(searchTermChangeListener)
    this.fontsizeSubscription.unsubscribe()
    getSelectionModel.getSelectedItems.removeListener(selectedListener)
    this.fontsizeProperty.unbind()
    this.firstVisibleTextCellIndexProperty.unbindBidirectional(firstVisibleTextCellIndexProperty)
    this.lastVisibleTextCellIndexProperty.unbindBidirectional(lastVisibleTextCellIndexProperty)
    this.skinSubscriber.unsubscribe()
    this.someScrollBarSubscription.foreach(_.unsubscribe())
    this.maxSizeProperty.unbind()

  addEventHandler(DataModelEvent.ScrollToActiveLogEntry, (e: ScrollToActiveLogEntry) =>
    scrollToActiveLogEntry()
    e.consume()
  )


