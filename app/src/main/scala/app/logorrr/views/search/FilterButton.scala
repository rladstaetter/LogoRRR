package app.logorrr.views.search

import app.logorrr.views.ops.RectButton
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.{ContentDisplay, ToggleButton, Tooltip}

object FilterButton {
  class RemoveFilterbutton(filter: Filter, removeFilter: Filter => Unit) extends RectButton(10, 10, filter.color, "remove") {
    setOnAction(_ => removeFilter(filter))
    setStyle(
      """-fx-padding: 1 4 1 4;
        |-fx-background-radius: 0;
        |""".stripMargin)
  }

}

/**
 * Displays a search term and triggers displaying the results.
 */
class FilterButton(val filter: Filter
                   , i: Int
                   , updateActiveFilter: () => Unit
                   , removeFilter: Filter => Unit) extends ToggleButton(filter.pattern) {

  setTooltip(new Tooltip(if (i == 1) "one item found" else s"$i items found"))

  if (!isUnclassified) {
    setContentDisplay(ContentDisplay.RIGHT)
    setGraphic(new FilterButton.RemoveFilterbutton(filter, removeFilter))
  }
  setSelected(filter.active)

  selectedProperty().addListener(new InvalidationListener {
    // if any of the buttons changes its selected value, reevaluate predicate
    // and thus change contents of all views which display filtered List
    override def invalidated(observable: Observable): Unit = updateActiveFilter()
  })

  def isUnclassified: Boolean = filter.isInstanceOf[UnclassifiedFilter]

}
