package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, TimeSettings}
import app.logorrr.model.{BoundId, DateFilterEvent, LogEntry, RemoveDateFilterEvent}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.LogFilePane
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Button

import java.util

object TimestampFormatResetButton extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampFormatResetButton])

/** reset log timestamp settings to default values */
class TimestampFormatResetButton(logFilePane: LogFilePane
                                 , closeStage: => Unit)
  extends Button("reset & close") with BoundId(TimestampFormatResetButton.uiNode(_).value):

  setAlignment(Pos.CENTER)
  setPrefWidth(100)

  setOnAction:
    _ =>
      logFilePane.fireEvent(new RemoveDateFilterEvent())
      closeStage

