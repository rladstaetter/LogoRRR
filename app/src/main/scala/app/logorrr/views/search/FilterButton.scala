package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.{CanLog, HashUtil}
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
                   , removeFilter: Filter => Unit) extends ToggleButton(filter.pattern) with CanLog {

  setId(FilterButton.uiNode(fileId, filter).value)
  setTooltip(new Tooltip(if (i == 1) "one item found" else s"$i items found"))

  if (!isUnclassified) {
    setContentDisplay(ContentDisplay.RIGHT)
    setGraphic(new RemoveFilterbutton(fileId, filter, removeFilter))
  }
  setSelected(filter.active)
//  logTrace(s"Fb: ${fileId.fileName} / ${filter.pattern}: setting selected to ${filter.active}")

  selectedProperty().addListener(new InvalidationListener {
    // if any of the buttons changes its selected value, reevaluate predicate
    // and thus change contents of all views which display filtered List
    override def invalidated(observable: Observable): Unit = {
      updateActiveFilter()
    }
  })
/*
  selectedProperty().addListener(new ChangeListener[lang.Boolean] {
    override def changed(observableValue: ObservableValue[_ <: lang.Boolean], t: lang.Boolean, t1: lang.Boolean): Unit = {
      val msg = s"Fb: ${fileId.fileName} / ${filter.pattern} Change from ${t} to ${t1}"
      logTrace(msg)
    }
  })
*/
  def isUnclassified: Boolean = filter.isInstanceOf[UnclassifiedFilter]

}
