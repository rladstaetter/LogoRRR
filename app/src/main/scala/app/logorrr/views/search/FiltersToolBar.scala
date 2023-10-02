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

  var filterButtons: Map[Filter, FilterButton] = Map[Filter, FilterButton]()

  var someUnclassifiedFilter: Option[(Filter, FilterButton)] = None

  var occurrences: Map[Filter, Int] = Map().withDefaultValue(0)

  /** will be bound to the current active filter list */
  val filtersProperty = new SimpleListProperty[Filter]()

  init()

  private def init(): Unit = {
    filtersProperty.addListener(JfxUtils.mkListChangeListener[Filter](processFiltersChange))
    updateUnclassified()
  }


  /** if list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[_ <: Filter]): Unit = {
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

  private def updateOccurrences(sf: Filter): Unit = {
    occurrences = occurrences + (sf -> filteredList.getSource.asScala.count(e => sf.matches(e.value)))
  }

  private def updateUnclassified(): Unit = {
    val unclassified = new UnclassifiedFilter(filterButtons.keySet)
    updateOccurrences(unclassified)
    val filterButton = new FilterButton(unclassified, occurrences(unclassified), updateActiveFilter, removeFilter)
    someUnclassifiedFilter.foreach(ftb => getItems.remove(ftb._2))
    getItems.add(0, filterButton)
    someUnclassifiedFilter = Option((unclassified, filterButton))
    updateActiveFilter()
  }

  private def addFilterButton(filter: Filter): Unit = {
    updateOccurrences(filter)
    val searchTag = new FilterButton(filter, occurrences(filter), updateActiveFilter, removeFilter)
    filter.bind(searchTag)
    getItems.add(searchTag)
    filterButtons = filterButtons.updated(filter, searchTag)
  }

  private def removeFilterButton(filter: Filter): Unit = {
    val button = filterButtons(filter)
    filter.unbind(button)
    getItems.remove(button)
    filterButtons = filterButtons.removed(filter)
  }

  def activeFilters(): Seq[Filter] = {
    (for (i <- getItems.asScala) yield {
      val st = i.asInstanceOf[FilterButton]
      if (st.isUnclassified) {
        None
      } else {
        Option(st.filter.withActive())
      }
    }).flatten.toSeq
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

  def updateActiveFilter(): Unit = {
    filteredList.setPredicate((entry: LogEntry) => computeCurrentFilter().matches(entry.value))
  }


}


