package app.logorrr.views.search

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.searchterm.SearchTermButton
import app.logorrr.views.{MutableSearchTerm, SearchTerm}
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.collections.transformation.FilteredList
import javafx.scene.control.ToolBar

import scala.jdk.CollectionConverters._

/** A toolbar with buttons which filter log events */
object SearchTermToolBar {

}


/**
 * Depending on buttons pressed, filteredList will be mutated to show only selected items.
 *
 * @param filteredList list of entries which are displayed (can be filtered via buttons)
 */
class SearchTermToolBar(mutLogFileSettings: MutLogFileSettings
                        , filteredList: FilteredList[LogEntry]
                        , removeFilter: MutableSearchTerm => Unit) extends ToolBar {

  setMaxHeight(Double.PositiveInfinity)

  var occurrences: Map[MutableSearchTerm, Int] = Map().withDefaultValue(0)

  /** will be bound to the current active filter list */
  val searchTermsProperty = new SimpleListProperty[MutableSearchTerm]()

  init()

  private def init(): Unit = {
    searchTermsProperty.addListener(JfxUtils.mkListChangeListener[MutableSearchTerm](processFiltersChange))
    updateUnclassified()
  }


  /** if filter list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[_ <: MutableSearchTerm]): Unit = {
    while (change.next()) {
      if (change.wasAdded()) {
        change.getAddedSubList.asScala.foreach(addFilterButton)
        updateUnclassified()
      } else if (change.wasRemoved()) {
        change.getRemoved.asScala.foreach(removeFilterButton)
        updateUnclassified()
      }
    }
  }

  private def updateOccurrences(sf: MutableSearchTerm): Unit = {
    occurrences = occurrences + (sf -> filteredList.getSource.asScala.count(e => sf.matches(e.value)))
  }

  private def updateUnclassified(): Unit = {
    val unclassified = MutableSearchTermUnclassified(mutLogFileSettings.filterButtons.keySet)
    val filterButton: SearchTermButton = updateOccurrencesAndFilter(unclassified)
    mutLogFileSettings.someUnclassifiedFilter.foreach(ftb => getItems.remove(ftb._2))
    getItems.add(0, filterButton)
    mutLogFileSettings.someUnclassifiedFilter = Option((unclassified, filterButton))
    mutLogFileSettings.updateActiveFilter(filteredList)
  }

  private def addFilterButton(filter: MutableSearchTerm): Unit = {
    val filterButton = updateOccurrencesAndFilter(filter)
    filter.bind(filterButton.selectedProperty())
    filter.activeProperty.bind(filterButton.selectedProperty())
    getItems.add(filterButton)
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.updated(filter, filterButton)
  }

  private def updateOccurrencesAndFilter(unclassified: MutableSearchTerm): SearchTermButton = {
    updateOccurrences(unclassified)
    new SearchTermButton(mutLogFileSettings.getFileId, unclassified, occurrences(unclassified), mutLogFileSettings.updateActiveFilter(filteredList), removeFilter)
  }

  private def removeFilterButton(filter: MutableSearchTerm): Unit = {
    val button = mutLogFileSettings.filterButtons(filter)
    filter.unbind()
    getItems.remove(button)
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.removed(filter)
  }

  def activeFilters(): Seq[SearchTerm] = {
    (for (i <- getItems.asScala) yield {
      val st = i.asInstanceOf[SearchTermButton]
      if (st.isUnclassified) {
        None
      } else {
        Option(new SearchTerm(st.searchTerm.getPredicate.description, st.searchTerm.getColor, st.searchTerm.isActive))
      }
    }).flatten.toSeq
  }


}


