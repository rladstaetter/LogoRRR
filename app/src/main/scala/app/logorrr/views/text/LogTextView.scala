package app.logorrr.views.text

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundFileId, LogEntry}
import app.logorrr.util.{JetbrainsMonoFontStyleBinding, JfxUtils}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.text.contextactions.{CopyEntriesMenuItem, IgnoreAboveMenuItem, IgnoreBelowMenuItem}
import javafx.beans.binding.Bindings
import javafx.beans.property.{Property, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.collections.transformation.FilteredList
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.util.Subscription
import net.ladstatt.util.log.TinyLog

import scala.jdk.CollectionConverters.*

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
  private var scrollBarSubscription: Option[Subscription] = None

  /** recalculated if searchterms change */
  val searchTerms: SimpleListProperty[(String, Color)] = SimpleListProperty[(String, Color)]()

  /**
   * to observe the visible text and mark it in the boxview
   */
  private val skinSubscriber = skinProperty.subscribe(_ => {
    scrollBarSubscription =
      ListViewHelper.findScrollBar(this).map(_.valueProperty.subscribe(_ => {
        val (first, last) = ListViewHelper.getVisibleRange(this)
        firstVisibleTextCellIndexProperty.set(first)
        lastVisibleTextCellIndexProperty.set(last)
      }))
  })

  /** contains number of digits of max size of filtered list */
  private val maxSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty() {
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

  def init(fileIdProperty: SimpleObjectProperty[FileId]
           , selectedLineNumberProperty: Property[Number]
           , fontsizeProperty: Property[Number]
           , firstVisibleTextCellIndexProperty: Property[Number]
           , lastVisibleTextCellIndexProperty: Property[Number]
           , mutSearchTerms: SimpleListProperty[MutableSearchTerm]
          ): Unit =
    bind(fileIdProperty
      , selectedLineNumberProperty
      , fontsizeProperty
      , firstVisibleTextCellIndexProperty
      , lastVisibleTextCellIndexProperty)
    getStylesheets.add(getClass.getResource("/app/logorrr/LogTextView.css").toExternalForm)
    setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell(fontsizeProperty))
    setItems(filteredList)
    getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
    searchTerms.bind(new MutSearchTermBinding(mutSearchTerms))

  /** clean up listeners */
  def shutdown(selectedLineNumberProperty: Property[Number]
               , firstVisibleTextCellIndexProperty: Property[Number]
               , lastVisibleTextCellIndexProperty: Property[Number]
              ): Unit =
    selectedItemSubscription.unsubscribe()
    fontsizeSubscription.unsubscribe()
    unbind(selectedLineNumberProperty, firstVisibleTextCellIndexProperty, lastVisibleTextCellIndexProperty)
    skinSubscriber.unsubscribe()
    scrollBarSubscription.foreach(_.unsubscribe())
    searchTerms.unbind()
    maxSizeProperty.unbind()

  class LogEntryListCell(fontSizeProperty: Property[Number]) extends ListCell[LogEntry]:
    setGraphic(null)

    override def updateItem(t: LogEntry, b: Boolean): Unit =
      super.updateItem(t, b)
      styleProperty().unbind()
      Option(t) match
        case Some(e) =>
          styleProperty().bind(new JetbrainsMonoFontStyleBinding(fontSizeProperty))
          val entry = LogTextViewLabel(e
            , maxSizeProperty.get()
            , searchTerms.asScala.toSeq
            , fontSizeProperty)
          setGraphic(entry)

          val copySelectionMenuItem = new CopyEntriesMenuItem(getSelectionModel)
          val ignoreAboveMenuItem = new IgnoreAboveMenuItem(selectedLineNumberProperty
            , e
            , filteredList
            , scrollToActiveLogEntry)
          val ignoreBelowMenuItem = new IgnoreBelowMenuItem(e, filteredList)
          val menu = new ContextMenu(copySelectionMenuItem, ignoreAboveMenuItem, ignoreBelowMenuItem)
          setContextMenu(menu)
        case None =>
          styleProperty().unbind()
          Option(getGraphic) match {
            case Some(label: LogTextViewLabel) => LogTextViewLabel.unbind(label)
            case _ =>
          }
          setGraphic(null)
          setContextMenu(null)


  def bind(fileIdProperty: SimpleObjectProperty[FileId]
           , selectedLineNumberProperty: Property[Number]
           , fontSizeProperty: Property[Number]
           , firstVisibleTextCellIndexProperty: Property[Number]
           , lastVisibleTextCellIndexProperty: Property[Number]
          ): Unit = {
    bindIdProperty(fileIdProperty)
    this.selectedLineNumberProperty.bindBidirectional(selectedLineNumberProperty)
    this.fontsizeProperty.bind(fontSizeProperty)
    this.firstVisibleTextCellIndexProperty.bindBidirectional(firstVisibleTextCellIndexProperty)
    this.lastVisibleTextCellIndexProperty.bindBidirectional(lastVisibleTextCellIndexProperty)


  }

  def unbind(selectedLineNumberProperty: Property[Number]
             , firstVisibleTextCellIndexProperty: Property[Number]
             , lastVisibleTextCellIndexProperty: Property[Number]
            ): Unit = {
    unbindIdProperty()
    this.selectedLineNumberProperty.unbindBidirectional(selectedLineNumberProperty)
    this.fontsizeProperty.unbind()
    this.firstVisibleTextCellIndexProperty.unbindBidirectional(firstVisibleTextCellIndexProperty)
    this.lastVisibleTextCellIndexProperty.unbindBidirectional(lastVisibleTextCellIndexProperty)
  }
  

