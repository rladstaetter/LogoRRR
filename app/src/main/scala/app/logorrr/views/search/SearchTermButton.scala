package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.{MutableSearchTerm, UiNode, UiNodeSearchTermAware}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.{ContentDisplay, ToggleButton, Tooltip}
import net.ladstatt.util.log.CanLog

object SearchTermButton extends UiNodeSearchTermAware {

  override def uiNode(fileId: FileId, searchTerm: MutableSearchTerm): UiNode = UiNode(classOf[SearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm.getPredicate.description))

}

/**
 * Displays a search term and triggers displaying the results.
 */
class SearchTermButton(val fileId: FileId
                       , val filter: MutableSearchTerm
                       , hits: Int
                       , updateActiveFilter: => Unit
                       , removeFilter: MutableSearchTerm => Unit) extends ToggleButton(filter.getPredicate.description) with CanLog {

  setId(SearchTermButton.uiNode(fileId, filter).value)
  setTooltip(new Tooltip(if (hits == 1) "one item found" else s"$hits items found"))

  if (!isUnclassified) {
    setContentDisplay(ContentDisplay.RIGHT)
    setGraphic(new RemoveSearchTermButton(fileId, filter, removeFilter))
  }
  setSelected(filter.isActive)

  selectedProperty().addListener(new InvalidationListener {
    // if any of the buttons changes its selected value, reevaluate predicate
    // and thus change contents of all views which display filtered List
    override def invalidated(observable: Observable): Unit = updateActiveFilter

  })

  def isUnclassified: Boolean = filter.isInstanceOf[MutableSearchTermUnclassified]

}
