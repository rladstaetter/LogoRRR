package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.ops.RectButton
import app.logorrr.views.{UiNode, UiNodeFilterAware}

object RemoveFilterbutton extends UiNodeFilterAware {

  override def uiNode(fileId: FileId, filter: Filter): UiNode = UiNode(classOf[RemoveFilterbutton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + filter.pattern))
}

class RemoveFilterbutton(fileId: FileId, filter: Filter, removeFilter: Filter => Unit) extends RectButton(10, 10, filter.color, "remove") {
  setId(RemoveFilterbutton.uiNode(fileId, filter).value)
  setOnAction(_ => removeFilter(filter))
  setStyle(
    """-fx-padding: 1 4 1 4;
      |-fx-background-radius: 0;
      |""".stripMargin)
}