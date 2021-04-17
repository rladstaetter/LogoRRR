package app.logorrr.views

import javafx.beans.property.SimpleListProperty
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.ListChangeListener
import javafx.collections.transformation.FilteredList
import javafx.event.ActionEvent
import javafx.scene.control.{Button, ToggleButton, ToolBar}
import javafx.scene.shape.Rectangle
import app.logorrr._

import java.text.DecimalFormat
import scala.jdk.CollectionConverters._

/** A toolbar with buttons which filter log events */
object FilterButtonsToolBar {

  val percentFormatter = new DecimalFormat("#.##")

  def percentAsString(value: Int, totalSize: Int): String = {
    percentFormatter.format((100 * value.toDouble) / totalSize.toDouble) + "%"
  }


}


/**
 * Depending on buttons pressed, filteredList will be mutated to show only selected items.
 *
 * @param filteredList list of entries which are displayed (can be filtered via buttons)
 * @param totalSize    number of all entries
 */
class FilterButtonsToolBar(logView: LogView
                           , filteredList: FilteredList[LogEntry]
                           , totalSize: Int) extends ToolBar {

  val filtersProperty = new SimpleListProperty[Filter]()

  filtersProperty.addListener(new ListChangeListener[Filter] {
    override def onChanged(change: ListChangeListener.Change[_ <: Filter]): Unit = {
      while (change.next()) {
        if (change.wasAdded()) {
          for (f <- change.getAddedSubList.asScala) {
            addFilter(f)
          }
          updateUnclassified()
        } else if (change.wasRemoved()) {
          for (f <- change.getRemoved.asScala) {
            removeFilter(f)
          }
          updateUnclassified()
        }
      }
    }
  })


  var filterButtons: Map[Filter, SearchTag] = Map[Filter, SearchTag]()

  var someUnclassifiedFilter: Option[(Filter, SearchTag)] = None

  var occurences: Map[Filter, Int] = Map().withDefaultValue(0)

  def allFilters: Set[Filter] = filterButtons.keySet ++ someUnclassifiedFilter.map(x => Set(x._1)).getOrElse(Set())

  private def updateOccurrences(sf: Filter): Unit = {
    occurences = occurences + (sf -> filteredList.getSource.asScala.count(e => sf.applyMatch(e.value)))
  }

  private def updateUnclassified(): Unit = {
    val unclassified = InverseFilter(filterButtons.keySet)
    updateOccurrences(unclassified)
    val tb = mkToggleButton(unclassified)
    someUnclassifiedFilter.foreach(ftb => getItems.remove(ftb._2))
    getItems.add(0, tb)
    someUnclassifiedFilter = Option((unclassified, tb))
    updateActiveFilter()
  }

  /**
   * Filters are only active if selected.
   *
   * Unclassifiedfilter gets an extra handling since it depends on other filters
   *
   * @return
   */
  def computeCurrentFilter(): Filter = {
    AtLeastOneMatchFilter(someUnclassifiedFilter.map(fst => if (fst._2.toggleButton.isSelected) Set(fst._1) else Set()).getOrElse(Set()) ++
      filterButtons.filter(fst => fst._2.toggleButton.isSelected).keySet)
  }

  private def addFilter(filter: Filter): Unit = {
    updateOccurrences(filter)
    val tButton = mkToggleButton(filter)
    getItems.add(tButton)
    filterButtons = filterButtons + (filter -> tButton)
  }

  private def removeFilter(sf: Filter): Unit = {
    getItems.remove(filterButtons(sf))
    filterButtons = filterButtons - sf

  }

  def updateActiveFilter(): Unit = {
    val filter = computeCurrentFilter()
    filteredList.setPredicate((entry: LogEntry) => filter.applyMatch(entry.value))
  }

  private def mkToggleButton(sf: Filter): SearchTag = {
    val buttonTitle = sf.title + ": " + occurences(sf) + " " + FilterButtonsToolBar.percentAsString(occurences(sf), totalSize)
    val button = new ToggleButton(buttonTitle)
    val r = new Rectangle(10, 10)
    r.setFill(sf.color)
    button.setGraphic(r)
    button.setSelected(true)


    button.selectedProperty().addListener(new InvalidationListener {
      // if any of the buttons changes its selected value, reevaluate predicate
      // and thus change contents of all views which display filtered List
      override def invalidated(observable: Observable): Unit = updateActiveFilter()
    })

    /** filters can be removed, in this case update display */
    val removeButton = new Button("x")
    sf match {
      // 'unclassified' entries are shown by 'inversefilter'
      case _: InverseFilter => removeButton.setDisable(true) // disable 'unclassified' 'x' button
      case _ =>
    }
    removeButton.setOnAction((t: ActionEvent) => logView.removeFilter(sf))

    new SearchTag(button, removeButton)
  }
}

