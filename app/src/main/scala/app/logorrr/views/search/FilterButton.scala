package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.search.filter.UnclassifiedFilter
import app.logorrr.views.{MutFilter, UiNode, UiNodeFilterAware}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.{ContentDisplay, ToggleButton, Tooltip}
import net.ladstatt.util.log.CanLog

object FilterButton extends UiNodeFilterAware {

  override def uiNode(fileId: FileId, filter: MutFilter): UiNode = UiNode(classOf[FilterButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + filter.getPredicate.description))

}

/**
 * Displays a search term and triggers displaying the results.
 */
class FilterButton(val fileId: FileId
                   , val filter: MutFilter
                   , hits: Int
                   , updateActiveFilter: => Unit
                   , removeFilter: MutFilter => Unit) extends ToggleButton(filter.getPredicate.description) with CanLog {

  setId(FilterButton.uiNode(fileId, filter).value)
  setTooltip(new Tooltip(if (hits == 1) "one item found" else s"$hits items found"))

  if (!isUnclassified) {
    setContentDisplay(ContentDisplay.RIGHT)
    setGraphic(new RemoveFilterbutton(fileId, filter, removeFilter))
  }
  setSelected(filter.isActive)

  selectedProperty().addListener(new InvalidationListener {
    // if any of the buttons changes its selected value, reevaluate predicate
    // and thus change contents of all views which display filtered List
    override def invalidated(observable: Observable): Unit = updateActiveFilter

  })

  def isUnclassified: Boolean = filter.isInstanceOf[UnclassifiedFilter]

}
