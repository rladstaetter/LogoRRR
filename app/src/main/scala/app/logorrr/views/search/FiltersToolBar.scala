package app.logorrr.views.search

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.jfxbfr.{Filter, Fltr}
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
class FiltersToolBar(mutLogFileSettings: MutLogFileSettings
                     , filteredList: FilteredList[LogEntry]
                     , removeFilter: Fltr[_] => Unit) extends ToolBar {

  var occurrences: Map[Fltr[_], Int] = Map().withDefaultValue(0)

  /** will be bound to the current active filter list */
  val filtersProperty = new SimpleListProperty[Fltr[_]]()

  init()

  private def init(): Unit = {
    filtersProperty.addListener(JfxUtils.mkListChangeListener[Fltr[_]](processFiltersChange))
    updateUnclassified()
  }


  /** if filter list is changed in any way, react to this event and either add or remove filter from UI */
  private def processFiltersChange(change: ListChangeListener.Change[_ <: Fltr[_]]): Unit = {
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

  private def updateOccurrences(sf: Fltr[_]): Unit = {
    occurrences = occurrences + (sf -> filteredList.getSource.asScala.count(e => sf.matches(e.value)))
  }

  private def updateUnclassified(): Unit = {
    val unclassified = UnclassifiedFilter(mutLogFileSettings.filterButtons.keySet)
    updateOccurrences(unclassified)
    val filterButton = new FilterButton(mutLogFileSettings.getFileId, unclassified, occurrences(unclassified), mutLogFileSettings.updateActiveFilter(filteredList), removeFilter)
    mutLogFileSettings.someUnclassifiedFilter.foreach(ftb => getItems.remove(ftb._2))
    getItems.add(0, filterButton)
    mutLogFileSettings.someUnclassifiedFilter = Option((unclassified, filterButton))
    mutLogFileSettings.updateActiveFilter(filteredList)
  }

  private def addFilterButton(filter: Fltr[_]): Unit = {
    updateOccurrences(filter)
    val filterButton =
      new FilterButton(
        mutLogFileSettings.getFileId
        , filter
        , occurrences(filter)
        , mutLogFileSettings.updateActiveFilter(filteredList)
        , removeFilter)

    filter.bind(filterButton.selectedProperty())
    filter.activeProperty.bind(filterButton.selectedProperty())
    getItems.add(filterButton)
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.updated(filter, filterButton)
  }

  private def removeFilterButton(filter: Fltr[_]): Unit = {
    val button = mutLogFileSettings.filterButtons(filter)
    filter.unbind()
    getItems.remove(button)
    mutLogFileSettings.filterButtons = mutLogFileSettings.filterButtons.removed(filter)
  }

  def activeFilters(): Seq[Filter] = {
    (for (i <- getItems.asScala) yield {
      val st = i.asInstanceOf[FilterButton]
      if (st.isUnclassified) {
        None
      } else {
        Option(new Filter(st.filter.getPattern, st.filter.getColor, st.filter.isActive))
      }
    }).flatten.toSeq
  }


}


