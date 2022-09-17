package app.logorrr.views.search

import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.{Button, ToggleButton}
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


object SearchTag {

  def apply(filter: Filter
            , occurrences: Map[Filter, Int]
            , totalSize: Int
            , updateActiveFilter: () => Unit
            , removeFilter: Filter => Unit): SearchTag = {
    val i = occurrences(filter)
    val buttonTitle = s"${filter.pattern} $i"
    val button = new ToggleButton(buttonTitle)
    val r = new Rectangle(10, 10)
    r.setFill(filter.color)
    r.setStroke(Color.WHITE)
    button.setGraphic(r)
    button.setSelected(true)


    button.selectedProperty().addListener(new InvalidationListener {
      // if any of the buttons changes its selected value, reevaluate predicate
      // and thus change contents of all views which display filtered List
      override def invalidated(observable: Observable): Unit = updateActiveFilter()
    })

    /** filters can be removed, in this case update display */
    val removeButton = new FiltersToolBar.RemoveButton(filter, removeFilter)

    new SearchTag(button, removeButton)
  }

}

/**
 * Groups a toggle button to activate a filter and a button to remove it
 */
class SearchTag(val toggleButton: ToggleButton, val closeButton: Button) extends HBox {
  getChildren.addAll(toggleButton, closeButton)
}
