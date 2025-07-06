package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.jfxbfr.Fltr
import app.logorrr.util.HashUtil
import app.logorrr.views.{UiNode, UiNodeFilterAware}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.{ContentDisplay, ToggleButton, Tooltip}
import net.ladstatt.util.log.CanLog

object FilterButton extends UiNodeFilterAware {

  override def uiNode(fileId: FileId, filter: Fltr[_]): UiNode = UiNode(classOf[FilterButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + filter.getPattern))

}

/**
 * Displays a search term and triggers displaying the results.
 */
class FilterButton(val fileId: FileId
                   , val filter: Fltr[_]
                   , i: Int
                   , updateActiveFilter: => Unit
                   , removeFilter: Fltr[_] => Unit) extends ToggleButton(filter.getPattern) with CanLog {

  setId(FilterButton.uiNode(fileId, filter).value)
  setTooltip(new Tooltip(if (i == 1) "one item found" else s"$i items found"))

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
