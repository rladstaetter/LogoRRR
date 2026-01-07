package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.UiNode
import javafx.scene.control.{Label, Tooltip}
import javafx.scene.input.MouseEvent

object LogViewLabel {
  def uiNode(id: FileId, lineNumber: Int, col: Int): UiNode = {
    new UiNode(s"${HashUtil.md5Sum(id.value)}${classOf[LogViewLabel].getSimpleName}-${lineNumber.toString}-${col.toString}")
  }
}

class LogViewLabel(fileId: FileId
                   , lineNumber: Int
                   , col: Int
                   , c: String
                   , applyStyleAtPos: Int => Unit) extends Label(c) {
  setId(LogViewLabel.uiNode(fileId, lineNumber, col).value)
  setUserData(col) // save position of label for later
  setOnMouseClicked((_: MouseEvent) => applyStyleAtPos(col))
  setTooltip(new Tooltip(s"column: ${col.toString}"))
  setOnMouseEntered(_ => {
    setStyle(
      """-fx-border-color: RED;
        |-fx-border-width: 0 0 0 3px;
        |""".stripMargin)
  })
  setOnMouseExited(_ => setStyle(""))
}