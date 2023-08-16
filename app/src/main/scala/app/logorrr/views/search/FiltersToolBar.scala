package app.logorrr.views.search

import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.collections.transformation.FilteredList
import javafx.scene.control.ToolBar

import scala.jdk.CollectionConverters._

/** A toolbar with buttons which filter log events */
object FiltersToolBar {
/*
  private val BackgroundSelectedStyle: String =
    """
      |-fx-background-color: GREEN;
      |-fx-border-width: 1px 1px 1px 1px;
      |-fx-border-color: RED;
      |""".stripMargin
*/
}


/**
 * Depending on buttons pressed, filteredList will be mutated to show only selected items.
 *
 * @param filteredList list of entries which are displayed (can be filtered via buttons)
 */
class FiltersToolBar(filteredList: FilteredList[LogEntry]
                     , removeFilter: Filter => Unit) extends ToolBar {

  //  setStyle(FiltersToolBar.BackgroundSelectedStyle)

  val filtersProperty = new SimpleListProperty[Filter]()

  filtersProperty.addListener(JfxUtils.mkListChangeListener[Filter](processFiltersChange))


  /** if list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[_ <: Filter]): Unit = {
    while (change.next()) {
      if (change.wasAdded()) {
        change.getAddedSubList.asScala.foreach(addSearchTag)
        updateUnclassified()
      } else if (change.wasRemoved()) {
        change.getRemoved.asScala.foreach(removeSearchTag)
        updateUnclassified()
      }
    }
  }

  var filterButtons: Map[Filter, SearchTag] = Map[Filter, SearchTag]()

  var someUnclassifiedFilter: Option[(Filter, SearchTag)] = None

  var occurrences: Map[Filter, Int] = Map().withDefaultValue(0)

  private def updateOccurrences(sf: Filter): Unit = {
    occurrences = occurrences + (sf -> filteredList.getSource.asScala.count(e => sf.applyMatch(e.value)))
  }

  private def updateUnclassified(): Unit = {
    val unclassified = new UnclassifiedFilter(filterButtons.keySet)
    updateOccurrences(unclassified)
    val searchTag = new SearchTag(unclassified, occurrences(unclassified), updateActiveFilter, removeFilter)
    someUnclassifiedFilter.foreach(ftb => getItems.remove(ftb._2))
    getItems.add(0, searchTag)
    someUnclassifiedFilter = Option((unclassified, searchTag))
    updateActiveFilter()
  }

  /**
   * Filters are only active if selected.
   *
   * UnclassifiedFilter gets an extra handling since it depends on other filters
   *
   * @return
   */
  def computeCurrentFilter(): Fltr = {
    new AnyFilter(someUnclassifiedFilter.map(fst => if (fst._2.isSelected) Set(fst._1) else Set()).getOrElse(Set()) ++
      filterButtons.filter(fst => fst._2.isSelected).keySet)
  }

  private def addSearchTag(filter: Filter): Unit = {
    updateOccurrences(filter)
    val searchTag = new SearchTag(filter, occurrences(filter), updateActiveFilter, removeFilter)
    getItems.add(searchTag)
    filterButtons = filterButtons + (filter -> searchTag)
  }

  private def removeSearchTag(filter: Filter): Unit = {
    getItems.remove(filterButtons(filter))
    filterButtons = filterButtons - filter
  }

  def updateActiveFilter(): Unit = {
    val filter = computeCurrentFilter()
    filteredList.setPredicate((entry: LogEntry) => filter.applyMatch(entry.value))
  }

  updateUnclassified()


}


