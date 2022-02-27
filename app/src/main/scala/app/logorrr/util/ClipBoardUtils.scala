package app.logorrr.util

import javafx.scene.input.{Clipboard, ClipboardContent}

object ClipBoardUtils {

  def copyToClipboardText(s: String): Unit = {
    val clipboard = Clipboard.getSystemClipboard()
    val content = new ClipboardContent()
    content.putString(s)
    clipboard.setContent(content)
  }
}
