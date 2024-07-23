package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.{UiNode, UiNodeFilterAware}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.{ContentDisplay, ToggleButton, Tooltip}

object FilterButton extends UiNodeFilterAware {

  override def uiNode(fileId: FileId, filter: Filter): UiNode = UiNode(classOf[FilterButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + filter.pattern))

}

/**
 * Displays a search term and triggers displaying the results.
 */
class FilterButton(val fileId: FileId
                   , val filter: Filter
                   , i: Int
                   , updateActiveFilter: () => Unit
                   , removeFilter: Filter => Unit) extends ToggleButton(filter.pattern) {

  setId(FilterButton.uiNode(fileId, filter).value)
  setTooltip(new Tooltip(if (i == 1) "one item found" else s"$i items found"))

  if (!isUnclassified) {
    setContentDisplay(ContentDisplay.RIGHT)
    setGraphic(new RemoveFilterbutton(fileId, filter, removeFilter))
  }
  setSelected(filter.active)

  selectedProperty().addListener(new InvalidationListener {
    // if any of the buttons changes its selected value, reevaluate predicate
    // and thus change contents of all views which display filtered List
    override def invalidated(observable: Observable): Unit = updateActiveFilter()
  })

  def isUnclassified: Boolean = filter.isInstanceOf[UnclassifiedFilter]

}
