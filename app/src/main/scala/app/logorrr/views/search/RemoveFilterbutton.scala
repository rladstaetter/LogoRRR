package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.ops.RectButton
import app.logorrr.views.{MutFilter, UiNode, UiNodeFilterAware}

object RemoveFilterbutton extends UiNodeFilterAware {

  override def uiNode(fileId: FileId, filter: MutFilter): UiNode = UiNode(classOf[RemoveFilterbutton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + filter.getPredicate.description))
}

class RemoveFilterbutton(fileId: FileId, filter: MutFilter, removeFilter: MutFilter => Unit) extends RectButton(10, 10, filter.getColor, "remove") {
  setId(RemoveFilterbutton.uiNode(fileId, filter).value)
  setOnAction(_ => removeFilter(filter))
  setStyle(
    """-fx-padding: 1 4 1 4;
      |-fx-background-radius: 0;
      |""".stripMargin)
}