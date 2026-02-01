package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, SimpleRange, TimestampSettings}
import app.logorrr.model.LogEntry
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.{OpsToolBar, TimestampSettingsRegion}
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
class TimestampFormatSetButton(mutLogFileSettings: MutLogFileSettings
                               , range: => Option[(Int, Int)]
                               , timeFormatTf: TextField
                               , chunkListView: ChunkListView[LogEntry]
                               , logEntries: ObservableList[LogEntry]
                               , tsRegion: TimestampSettingsRegion
                               , closeStage: => Unit) extends Button("Apply & close"):
  setId(TimestampFormatSetButton.uiNode(mutLogFileSettings.getFileId).value)
  setPrefWidth(300)
  setAlignment(Pos.CENTER)
  setOnAction(_ => {
    range match {
      // startcol has to be smaller than endCol (and also of a certain size)
      // and the timeFormat is set to something
      case Some((startCol, endCol)) if startCol < endCol && timeFormatTf.getText.nonEmpty =>
        val timestampSettings: TimestampSettings = TimestampSettings(startCol, endCol, timeFormatTf.getText.trim)
        mutLogFileSettings.setSomeLogEntryInstantFormat(Option(timestampSettings))
        LogoRRRGlobals.persist(LogoRRRGlobals.getSettings)

        chunkListView.removeInvalidationListener()
        var someFirstEntryTimestamp: Option[Instant] = None

        val tempList = new util.ArrayList[LogEntry]()
        for i <- 0 until logEntries.size() do {
          val e = logEntries.get(i)
          val someInstant = TimestampSettings.parseInstant(e.value, timestampSettings)
          if someFirstEntryTimestamp.isEmpty then {
            someFirstEntryTimestamp = someInstant
          }

          val someDiffFromStart: Option[Duration] = for
            firstEntry <- someFirstEntryTimestamp
            instant <- someInstant
          yield Duration.between(firstEntry, instant)

          tempList.add(e.copy(someInstant = someInstant, someDurationSinceFirstInstant = someDiffFromStart))
        }
        logEntries.setAll(tempList)
        // activate listener again
        chunkListView.addInvalidationListener()
        // update slider boundaries
        tsRegion.initializeRanges()
      // on any other case just return None
      case _ =>
        mutLogFileSettings.setSomeLogEntryInstantFormat(None)
    }

    closeStage
  })


