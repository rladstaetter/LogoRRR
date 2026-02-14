package app.logorrr.views.ops

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundId, LogEntry}
import app.logorrr.util.ClipBoardUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectPropertyBase, SimpleListProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

import java.time.Duration

object CopyLogButton extends UiNodeFileIdAware:

  def uiNode(id: FileId): UiNode = UiNode(id, classOf[CopyLogButton])


/**
 * Copy current contents to clipboard.
 */
class CopyLogButton extends Button with BoundId(CopyLogButton.uiNode(_).value):

  private val icon = new FontIcon(FontAwesomeSolid.COPY)
  private val iconLight = new FontIcon(FontAwesomeRegular.COPY)
  private val defaultToolTip = new Tooltip("copy current selection to clipboard")
  private val entries = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())

  setOnAction:
    _ =>
      val size = ClipBoardUtils.copyToClipboard(entries)
      defaultToolTip.setText(s"Copied $size entries to clipboard")
      val bounds = localToScreen(getBoundsInLocal)
      val x = bounds.getMinX
      val y = bounds.getMaxY
      defaultToolTip.show(this, x, y)
      mkTimer().start() // visual response to click

  private def mkTimer() = new PulsatingAnimationTimer(this, iconLight, icon, defaultToolTip, "copy current selection to clipboard", Duration.ofSeconds(1))

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , filteredList: ObservableList[LogEntry]): Unit =
    bindIdProperty(fileIdProperty)
    setGraphic(icon)
    setTooltip(defaultToolTip)
    entries.bindContent(filteredList)

  def shutdown(filteredList: ObservableList[LogEntry]): Unit =
    unbindIdProperty()
    entries.unbindContentBidirectional(filteredList)




