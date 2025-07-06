package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.jfxbfr.MutFilter
import app.logorrr.util.HashUtil
import app.logorrr.views.ops.RectButton
import app.logorrr.views.{UiNode, UiNodeFilterAware}

object RemoveFilterbutton extends UiNodeFilterAware {

  override def uiNode(fileId: FileId, filter: MutFilter[_]): UiNode = UiNode(classOf[RemoveFilterbutton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + filter.getPredicate.description))
}

class RemoveFilterbutton(fileId: FileId, filter: MutFilter[_], removeFilter: MutFilter[_] => Unit) extends RectButton(10, 10, filter.getColor, "remove") {
  setId(RemoveFilterbutton.uiNode(fileId, filter).value)
  setOnAction(_ => removeFilter(filter))
  setStyle(
    """-fx-padding: 1 4 1 4;
      |-fx-background-radius: 0;
      |""".stripMargin)
}