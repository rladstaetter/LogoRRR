package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.ColorUtil
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{ColorPicker, Tooltip}

object SearchColorPicker extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchColorPicker])
}

class SearchColorPicker(id : FileId) extends ColorPicker {
  setId(SearchColorPicker.uiNode(id).value)
  setValue(ColorUtil.randColor)
  setMaxWidth(46)
  setTooltip(new Tooltip("choose color"))
  // see https://stackoverflow.com/questions/45966844/how-to-change-the-icon-size-of-a-color-picker-in-javafx
  setStyle("""-fx-color-label-visible: false""".stripMargin)

}

