package app.logorrr.views.search.st

import app.logorrr.conf.SearchTerm
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.stg.{OpenStgEditorButton, StgChoiceBox}
import app.logorrr.views.search.{MutableSearchTerm, UnclassifiedSearchTerm}
import javafx.beans.property.SimpleListProperty
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ListChangeListener}
import javafx.scene.control.ToolBar
import net.ladstatt.util.log.TinyLog

import scala.jdk.CollectionConverters._

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
  val searchTermsProperty: SimpleListProperty[MutableSearchTerm] = new SimpleListProperty[MutableSearchTerm](FXCollections.observableArrayList())

  private val groupChoiceBox: StgChoiceBox =
    val gcb = new StgChoiceBox(mutLogFileSettings, searchTermsProperty)
    gcb.itemsProperty.set(mutLogFileSettings.searchTermGroupNames)
    mutLogFileSettings.getSomeSelectedSearchTermGroup.foreach(gcb.setValue)
    gcb

  val openStgEditor = new OpenStgEditorButton(mutLogFileSettings
    , mutLogFileSettings.getFileId
    , activeSearchTerms)

  init()

  private def init(): Unit =
    getItems.addAll(groupChoiceBox, openStgEditor)
    searchTermsProperty.addListener(JfxUtils.mkListChangeListener[MutableSearchTerm](processFiltersChange))
    updateUnclassified()
    searchTermsProperty.bind(mutLogFileSettings.mutSearchTerms)


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
    val unclassified = UnclassifiedSearchTerm(mutLogFileSettings.filterButtons.keySet)
    val searchTermButton: SearchTermButton = updateOccurrencesAndFilter(unclassified)
    mutLogFileSettings.someUnclassifiedFilter.foreach(ftb => getItems.remove(ftb._2))
    getItems.add(2, searchTermButton)
    mutLogFileSettings.someUnclassifiedFilter = Option((unclassified, searchTermButton))
    mutLogFileSettings.updateActiveFilter(filteredList)

  private def addSearchTermButton(filter: MutableSearchTerm): Unit =
    val filterButton = updateOccurrencesAndFilter(filter)
    filter.bind(filterButton.selectedProperty())
    filter.activeProperty.bind(filterButton.selectedProperty())
    getItems.add(filterButton)
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.updated(filter, filterButton)

  private def updateOccurrencesAndFilter(searchTerm: MutableSearchTerm): SearchTermButton =
    updateOccurrences(searchTerm)
    new SearchTermButton(
      mutLogFileSettings.getFileId
      , searchTerm
      , occurrences(searchTerm)
      , mutLogFileSettings.updateActiveFilter(filteredList)
      , mutLogFileSettings.mutSearchTerms.remove(_))

  private def removeSearchTermButton(filter: MutableSearchTerm): Unit =
    try
      val button = mutLogFileSettings.filterButtons(filter)
      getItems.remove(button)
    catch
      case e: Throwable => logException("Could not find or remove button", e)
    finally
      filter.unbind()
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.removed(filter)

  def activeSearchTerms(): Seq[SearchTerm] =
    (for i <- getItems.asScala yield {
      i match {
        case st: SearchTermButton =>
          if st.isUnclassified then {
            None
          } else {
            val searchTerm = st.searchTerm
            Option(new SearchTerm(searchTerm.getSearchTermAsString, searchTerm.getColor, searchTerm.isActive))
          }
        case _ => None
      }
    }).flatten.toSeq






