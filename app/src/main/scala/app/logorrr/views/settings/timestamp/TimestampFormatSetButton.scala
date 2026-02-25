package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, TimeSettings}
import app.logorrr.model.{BoundId, DateFilterEvent, LogEntry}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.LogFilePane
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.{Button, TextField}

import java.time.{Duration, Instant}
import java.util

object TimestampFormatSetButton extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampFormatSetButton])

/**
 * if ok button is clicked, log definition will be written, settings stage will be closed, associated logfile
 * definition will be updated
 * */
class TimestampFormatSetButton(logFilePane: LogFilePane
                               , mutLogFileSettings: MutLogFileSettings
                               , rangeProperty: SimpleObjectProperty[(Int, Int)]
                               , timeFormatTf: TextField
                               , chunkListView: ChunkListView[LogEntry]
                               , logEntries: ObservableList[LogEntry]
                               , tsRegion: TimestampSettingsRegion
                               , closeStage: => Unit) extends Button("apply & close") with BoundId(TimestampFormatSetButton.uiNode(_).value):
  setPrefWidth(300)
  setAlignment(Pos.CENTER)
  setOnAction:
    _ =>
      Option(rangeProperty.get(), timeFormatTf.getText.trim) match {
        // startcol has to be smaller than endCol (and also of a certain size)
        // and the timeFormat is set to something
        case Some((startCol, endCol), timeFormat) if startCol < endCol && timeFormat.nonEmpty =>
          logFilePane.fireEvent(DateFilterEvent(TimeSettings(startCol, endCol, timeFormat)))
        // on any other case just return None
        case _ =>
          mutLogFileSettings.setTimeSettings(TimeSettings.Invalid)
      }
      closeStage


