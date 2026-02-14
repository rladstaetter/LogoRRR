package app.logorrr.views.search.st

import app.logorrr.conf.SearchTerm
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.stg.{OpenStgEditorButton, StgChoiceBox}
import javafx.beans.binding.Bindings
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control.ToolBar
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog

import scala.jdk.CollectionConverters.*

/**
 * Depending on buttons pressed, filteredList will be mutated to show only selected items.
 *
 * @param filteredList list of entries which are displayed (can be filtered via buttons)
 */
class SearchTermToolBar(mutLogFileSettings: MutLogFileSettings
                        , filteredList: FilteredList[LogEntry]) extends ToolBar with TinyLog:

  setMaxHeight(Double.PositiveInfinity)

  var occurrences: Map[MutableSearchTerm, Int] = Map().withDefaultValue(0)

  /** will be bound to the current active filter list */
  val mutSearchTerms: ObservableList[MutableSearchTerm] = FXCollections.observableArrayList()

  private val groupChoiceBox: StgChoiceBox =
    val gcb = new StgChoiceBox(mutLogFileSettings, mutSearchTerms)
    gcb.itemsProperty.set(mutLogFileSettings.searchTermGroupNames)
    mutLogFileSettings.getSomeSelectedSearchTermGroup.foreach(gcb.setValue)
    gcb

  val openStgEditor = new OpenStgEditorButton(mutLogFileSettings
    , mutLogFileSettings.getFileId
    , activeSearchTerms)

  def init(window: Window): Unit =
    groupChoiceBox.init(mutLogFileSettings.fileIdProperty)
    openStgEditor.init(window, mutLogFileSettings.fileIdProperty)
    getItems.addAll(groupChoiceBox, openStgEditor)
    mutSearchTerms.addListener(JfxUtils.mkListChangeListener[MutableSearchTerm](processFiltersChange))
    updateUnclassified()
    Bindings.bindContentBidirectional(this.mutSearchTerms, mutLogFileSettings.mutSearchTerms)


  def shutdown(): Unit =
    groupChoiceBox.shutdown()
    openStgEditor.shutdown()
    Bindings.unbindContentBidirectional(this.mutSearchTerms, mutLogFileSettings.mutSearchTerms)


  /** if filter list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[? <: MutableSearchTerm]): Unit =
    while change.next() do
      if change.wasAdded() then
        change.getAddedSubList.asScala.foreach(addSearchTermButton)
        updateUnclassified()
      else if change.wasRemoved() then
        change.getRemoved.asScala.foreach(removeSearchTermButton)
        updateUnclassified()

  private def updateOccurrences(mutableSearchTerm: MutableSearchTerm): Unit =
    occurrences = occurrences + (mutableSearchTerm -> filteredList.getSource.asScala.count(e => mutableSearchTerm.test(e.value)))

  private def updateUnclassified(): Unit =
    val unclassified = MutableSearchTerm.mkUnclassified(mutLogFileSettings.filterButtons.keySet)
    val searchTermButton: SearchTermToggleButton = updateOccurrencesAndFilter(unclassified)
    mutLogFileSettings.someUnclassifiedFilter.foreach(ftb => {
      val btn = ftb._2
      btn.unbind()
      getItems.remove(btn)
    })
    getItems.add(2, searchTermButton)
    mutLogFileSettings.someUnclassifiedFilter = Option((unclassified, searchTermButton))
    mutLogFileSettings.updateActiveFilter(filteredList)

  private def addSearchTermButton(filter: MutableSearchTerm): Unit =
    val filterButton = updateOccurrencesAndFilter(filter)
    getItems.add(filterButton)
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.updated(filter, filterButton)

  private def updateOccurrencesAndFilter(searchTerm: MutableSearchTerm): SearchTermToggleButton =
    updateOccurrences(searchTerm)
    SearchTermToggleButton(
      mutLogFileSettings.getFileId
      , searchTerm
      , occurrences(searchTerm)
      , mutLogFileSettings.updateActiveFilter(filteredList)
      , mutLogFileSettings.mutSearchTerms.remove)

  private def removeSearchTermButton(filter: MutableSearchTerm): Unit =
    try
      val button: SearchTermToggleButton = mutLogFileSettings.filterButtons(filter)
      button.unbind()
      getItems.remove(button)
    catch
      case e: Throwable => logException("Could not find or remove button", e)
    finally
      filter.unbindActiveProperty()
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.removed(filter)

  def activeSearchTerms(): Seq[SearchTerm] =
    (for i <- getItems.asScala yield {
      i match {
        case st: SearchTermToggleButton =>
          if st.isUnclassified then {
            None
          } else {
            Option(st.getMutableSearchTerm.asSearchTerm)
          }
        case _ => None
      }
    }).flatten.toSeq






