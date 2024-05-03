package app.logorrr.views.search

import app.logorrr.util.HashUtil
import app.logorrr.views.ops.RectButton
import app.logorrr.views.{UiNode, UiNodeFilterAware}

object RemoveFilterbutton extends UiNodeFilterAware {

  override def uiNode(filter: Filter): UiNode = UiNode(classOf[RemoveFilterbutton].getSimpleName + "-" + HashUtil.md5Sum(filter.pattern))
}

class RemoveFilterbutton(filter: Filter, removeFilter: Filter => Unit) extends RectButton(10, 10, filter.color, "remove") {
  setId(RemoveFilterbutton.uiNode(filter).value)
  setOnAction(_ => removeFilter(filter))
  setStyle(
    """-fx-padding: 1 4 1 4;
      |-fx-background-radius: 0;
      |""".stripMargin)
}