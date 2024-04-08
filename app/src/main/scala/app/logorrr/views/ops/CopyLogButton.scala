package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.{UiNode, UiNodeAware}
import javafx.animation.AnimationTimer
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.input.{Clipboard, ClipboardContent}
import org.kordamp.ikonli.fontawesome5.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

import scala.collection.mutable.ListBuffer

object CopyLogButton extends UiNodeAware {

  def uiNode(id: FileId): UiNode = UiNode(id, classOf[CopyLogButton])

}


/**
 * Copy current contents to clipboard.
 *
 * @param logEntries current active log entries
 */
class CopyLogButton(id: FileId, logEntries: ObservableList[LogEntry]) extends Button {

  setId(CopyLogButton.uiNode(id).value)

  private val icon = new FontIcon(FontAwesomeSolid.COPY)
  private val iconLight = new FontIcon(FontAwesomeRegular.COPY)
  private val TooltipText = "copy current selection to clipboard"
  private val defaultToolTip = new Tooltip(TooltipText)

  init()

  def init(): Unit = {
    setGraphic(icon)
    setTooltip(defaultToolTip)

    setOnAction(_ => {
      val lb = new ListBuffer[String]
      logEntries.forEach(e => lb.addOne(e.value))
      copyToClipboard(lb.mkString("\n"))
      defaultToolTip.setText(s"Copied ${lb.size} entries to clipboard")
      val bounds = localToScreen(getBoundsInLocal)
      val x = bounds.getMinX
      val y = bounds.getMaxY
      defaultToolTip.show(this, x, y)

      mkTimer().start() // visual response to click
    })
  }

  def mkTimer(): AnimationTimer = new AnimationTimer() {
    private var startTime: Long = -1

    def handle(now: Long): Unit = {
      if (startTime < 0) {
        setGraphic(iconLight)
        startTime = now
      }

      val elapsedSeconds = (now - startTime) / 1_000_000_000.0

      // Stop the animation after 1 second
      if (elapsedSeconds > 1) {
        getGraphic.setOpacity(1) // Ensure it ends at full opacity
        setGraphic(icon)
        defaultToolTip.hide()
        defaultToolTip.setText(TooltipText)
        this.stop()
        return
      }
      val alpha = elapsedSeconds / 1 // From 0 to 1 in one second

      if (alpha <= 0.5) {
        // Fade out for the first half
        getGraphic.setOpacity(1 - 2 * alpha)
      } else {
        // Fade in for the second half
        getGraphic.setOpacity(2 * alpha - 1)
      }
    }
  }

  def copyToClipboard(text: String): Unit = {
    val clipboard = Clipboard.getSystemClipboard
    val content = new ClipboardContent()
    content.putString(text)
    clipboard.setContent(content)
  }
}
