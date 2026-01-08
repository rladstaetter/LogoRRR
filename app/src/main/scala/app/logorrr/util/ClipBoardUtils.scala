package app.logorrr.util

import app.logorrr.model.LogEntry
import javafx.collections.ObservableList
import javafx.scene.input.{Clipboard, ClipboardContent}

import scala.collection.mutable.ListBuffer

object ClipBoardUtils:

  def copyToClipboard(logEntries : ObservableList[LogEntry]) : Int =
    val lb = new ListBuffer[String]
    logEntries.forEach(e => lb.addOne(e.value))
    copyToClipboard(lb.mkString("\n"))
    lb.size

  def copyToClipboard(s: String): Unit =
    val clipboard = Clipboard.getSystemClipboard()
    val content = new ClipboardContent()
    content.putString(s)
    clipboard.setContent(content)

