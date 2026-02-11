package app.logorrr.views.text

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundFileId, LogEntry}
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.Bindings
import javafx.beans.property.{ObjectPropertyBase, Property, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.util.Subscription
import net.ladstatt.util.log.TinyLog

import java.util.function.Predicate
import scala.compiletime.uninitialized

object LogTextView extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogTextView])


class LogTextView(filteredList: FilteredList[LogEntry])
  extends ListView[LogEntry]
    with TinyLog with BoundFileId(LogTextView.uiNode(_).value):

  // property which contains selected line number, bidirectionally bound to MutLogFileSettings
  private val selectedLineNumberProperty = new SimpleIntegerProperty()
  private val selectedItemSubscription: Subscription =
    getSelectionModel.selectedItemProperty().subscribe(e => {
      Option(e) match {
        case Some(value) => selectedLineNumberProperty.set(value.lineNumber)
        case None =>
      }
    })

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
    getSelectionModel.clearAndSelect(relativeIndex)
    val cellHeight = fontsizeProperty.get()
    JfxUtils.scrollTo[LogEntry](this, cellHeight, relativeIndex)

  def scrollToActiveLogEntry(): Unit =
    if getHeight != 0 then
      val candidates = filteredList.filtered(l => l.lineNumber == selectedLineNumberProperty.get())
      if !candidates.isEmpty then
        Option(candidates.get(0)) match
          case Some(selectedEntry) =>
            scrollToItem(selectedEntry)
            // to trigger ChunkListView scrollTo and repaint
            selectedLineNumberProperty.set(selectedEntry.lineNumber)
          case None => // do nothing

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , selectedLineNumberProperty: Property[Number]
           , fontsizeProperty: Property[Number]
           , firstVisibleTextCellIndexProperty: Property[Number]
           , lastVisibleTextCellIndexProperty: Property[Number]
           , mutSearchTerms: ObservableList[MutableSearchTerm]
          ): Unit =
    bindIdProperty(fileIdProperty)
    val activeSearchTerms = new FilteredList[MutableSearchTerm](mutSearchTerms, _.isActive)
    activeSearchTerms.forEach(st => searchTermsAndColors.add((st.getValue,st.getColor)))

    this.searchTermChangeListener = new MutableSearchTermListener(activeSearchTerms, searchTermsAndColors, this)
    mutSearchTerms.addListener(searchTermChangeListener)
    // this.searchTermsAndColors.bind(new MutSearchTermBinding(mutSearchTerms))
    this.selectedLineNumberProperty.bindBidirectional(selectedLineNumberProperty)
    this.fontsizeProperty.bind(fontsizeProperty)
    this.firstVisibleTextCellIndexProperty.bindBidirectional(firstVisibleTextCellIndexProperty)
    this.lastVisibleTextCellIndexProperty.bindBidirectional(lastVisibleTextCellIndexProperty)
    setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell(filteredList, this.searchTermsAndColors, this.selectedLineNumberProperty, scrollToActiveLogEntry, this.fontsizeProperty, this.maxSizeProperty))
    setItems(filteredList)
    getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    getStylesheets.add(getClass.getResource("/app/logorrr/LogTextView.css").toExternalForm)


  /** clean up listeners */
  def shutdown(selectedLineNumberProperty: Property[Number]
               , firstVisibleTextCellIndexProperty: Property[Number]
               , lastVisibleTextCellIndexProperty: Property[Number]
               , mutableSearchTerms: ObservableList[MutableSearchTerm]
              ): Unit =
    unbindIdProperty()

    mutableSearchTerms.removeListener(searchTermChangeListener)
    this.selectedItemSubscription.unsubscribe()
    this.fontsizeSubscription.unsubscribe()
    this.selectedLineNumberProperty.unbindBidirectional(selectedLineNumberProperty)
    this.fontsizeProperty.unbind()
    this.firstVisibleTextCellIndexProperty.unbindBidirectional(firstVisibleTextCellIndexProperty)
    this.lastVisibleTextCellIndexProperty.unbindBidirectional(lastVisibleTextCellIndexProperty)
    this.skinSubscriber.unsubscribe()
    this.someScrollBarSubscription.foreach(_.unsubscribe())
    this.maxSizeProperty.unbind()



