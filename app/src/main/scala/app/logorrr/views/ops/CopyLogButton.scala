package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.util.ClipBoardUtils
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

import java.time.Duration

object CopyLogButton extends UiNodeFileIdAware {

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
      val size = ClipBoardUtils.copyToClipboard(logEntries)
      defaultToolTip.setText(s"Copied $size entries to clipboard")
      val bounds = localToScreen(getBoundsInLocal)
      val x = bounds.getMinX
      val y = bounds.getMaxY
      defaultToolTip.show(this, x, y)

      mkTimer().start() // visual response to click
    })
  }

  def mkTimer() = new PulsatingAnimationTimer(this, iconLight, icon, defaultToolTip, TooltipText, Duration.ofSeconds(1))

}


