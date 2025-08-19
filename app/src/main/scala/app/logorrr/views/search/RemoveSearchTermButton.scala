package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.ops.RectButton
import app.logorrr.views.{MutableSearchTerm, UiNode, UiNodeSearchTermAware}

object RemoveSearchTermButton extends UiNodeSearchTermAware {

  override def uiNode(fileId: FileId, searchTerm: MutableSearchTerm): UiNode = UiNode(classOf[RemoveSearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm.getPredicate.description))
}

class RemoveSearchTermButton(fileId: FileId, filter: MutableSearchTerm, removeFilter: MutableSearchTerm => Unit) extends RectButton(10, 10, filter.getColor, "remove") {
  setId(RemoveSearchTermButton.uiNode(fileId, filter).value)
  setOnAction(_ => removeFilter(filter))
  setStyle(
    """-fx-padding: 1 4 1 4;
      |-fx-background-radius: 0;
      |""".stripMargin)
}