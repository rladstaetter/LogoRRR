package app.logorrr.views.ops

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.input.{Clipboard, ClipboardContent}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

import scala.collection.mutable.ListBuffer

class CopyLogButton(logEntries: ObservableList[LogEntry]) extends Button {
  private val icon = new FontIcon(FontAwesomeSolid.COPY)
  setGraphic(icon)
  setTooltip(new Tooltip("copy current selection to clipboard"))
  setOnAction(_ => {
    val lb = new ListBuffer[String]
    logEntries.forEach(e => lb.addOne(e.value))
    copyToClipboard(lb.mkString("\n"))
  })

  def copyToClipboard(text: String): Unit = {
    val clipboard = Clipboard.getSystemClipboard
    val content = new ClipboardContent()
    content.putString(text)
    clipboard.setContent(content)
  }
}
