package app.logorrr.views.ops

import app.logorrr.conf.FileId
import app.logorrr.model.{BoundId, LogEntry}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.{ObjectPropertyBase, SimpleListProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

object ClearLogButton extends UiNodeFileIdAware:

  def uiNode(id: FileId): UiNode = UiNode(id, classOf[ClearLogButton])


class ClearLogButton extends Button with BoundId(ClearLogButton.uiNode(_).value):
  private val icon = new FontIcon(FontAwesomeSolid.TRASH)
  setGraphic(icon)
  setTooltip(new Tooltip("clear log file"))
  setOnAction(_ => entries.clear())

  val entries = new SimpleListProperty[LogEntry](FXCollections.observableArrayList[LogEntry]())

  def init(fileIdProperty: ObjectPropertyBase[FileId], entries: ObservableList[LogEntry]): Unit = {
    bindIdProperty(fileIdProperty)
    this.entries.bindContentBidirectional(entries)
  }

  def shutdown(entries: ObservableList[LogEntry]): Unit = {
    unbindIdProperty()
    this.entries.unbindContentBidirectional(entries)
  }