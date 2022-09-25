package app.logorrr.views.search

import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.collections.transformation.FilteredList
import javafx.geometry.Insets
import javafx.scene.control.{Button, Label, ToolBar}

import scala.jdk.CollectionConverters._

/** A toolbar with buttons which filter log events */
object FiltersToolBar {

  class RemoveButton(filter: Filter, removeFilter: Filter => Unit) extends Button {
    setGraphic(new Label("ⓧ"))
    setDisable(filter.isInstanceOf[UnclassifiedFilter])
    setOnAction(_ => removeFilter(filter))
  }

}


/**
 * Depending on buttons pressed, filteredList will be mutated to show only selected items.
 *
 * @param filteredList list of entries which are displayed (can be filtered via buttons)
 */
class FiltersToolBar(filteredList: FilteredList[LogEntry]
                     , removeFilter: Filter => Unit) extends ToolBar {

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
    occurrences = occurrences + (sf -> filteredList.getSource.asScala.count(e => sf.matcher.applyMatch(e.value)))
  }

  private def updateUnclassified(): Unit = {
    val unclassified = new UnclassifiedFilter(filterButtons.keySet)
    updateOccurrences(unclassified)
    val searchTag = SearchTag(unclassified, occurrences, updateActiveFilter, removeFilter)
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
    new AnyFilter(someUnclassifiedFilter.map(fst => if (fst._2.toggleButton.isSelected) Set(fst._1) else Set()).getOrElse(Set()) ++
      filterButtons.filter(fst => fst._2.toggleButton.isSelected).keySet)
  }

  private def addSearchTag(filter: Filter): Unit = {
    updateOccurrences(filter)
    val searchTag = search.SearchTag(filter, occurrences,  updateActiveFilter, removeFilter)
    getItems.add(searchTag)
    filterButtons = filterButtons + (filter -> searchTag)
  }

  private def removeSearchTag(filter: Filter): Unit = {
    getItems.remove(filterButtons(filter))
    filterButtons = filterButtons - filter
  }

  def updateActiveFilter(): Unit = {
    val filter = computeCurrentFilter()
    filteredList.setPredicate((entry: LogEntry) => filter.matcher.applyMatch(entry.value))
  }

  updateUnclassified()


}


