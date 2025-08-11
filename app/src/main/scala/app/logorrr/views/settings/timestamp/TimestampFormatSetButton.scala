package app.logorrr.views.settings.timestamp

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.clv.ChunkListView
import app.logorrr.model.{LogEntry, TimestampSettings}
import app.logorrr.views.ops.time.TimeOpsToolBar
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.{Button, TextField}

import java.time.{Duration, Instant}
import java.util

object TimestampFormatSetButton extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampFormatSetButton])
}

/**
 * if ok button is clicked, log definition will be written, settings stage will be closed, associated logfile
 * definition will be updated
 * */
class TimestampFormatSetButton(mutLogFileSettings: MutLogFileSettings
                               , getRange: => SimpleRange
                               , timeFormatTf: TextField
                               , chunkListView: ChunkListView[LogEntry]
                               , logEntries: ObservableList[LogEntry]
                               , timeOpsToolBar: TimeOpsToolBar
                               , closeStage: => Unit) extends Button("set format") {
  setId(TimestampFormatSetButton.uiNode(mutLogFileSettings.getFileId).value)
  setPrefWidth(350)
  setAlignment(Pos.CENTER)
  setOnAction(_ => {
    val leif: TimestampSettings = TimestampSettings(getRange, timeFormatTf.getText.trim)
    mutLogFileSettings.setSomeLogEntryInstantFormat(Option(leif))
    LogoRRRGlobals.persist()
    // we have to deactivate this listener otherwise to many invalidationevents are triggered
    chunkListView.removeInvalidationListener()
    var someFirstEntryTimestamp: Option[Instant] = None

    val tempList = new util.ArrayList[LogEntry]()
    for (i <- 0 until logEntries.size()) {
      val e = logEntries.get(i)
      val someInstant = TimestampSettings.parseInstant(e.value, leif)
      if (someFirstEntryTimestamp.isEmpty) {
        someFirstEntryTimestamp = someInstant
      }

      val someDiffFromStart: Option[Duration] = for {
        firstEntry <- someFirstEntryTimestamp
        instant <- someInstant
      } yield Duration.between(firstEntry, instant)

      tempList.add(e.copy(someInstant = someInstant, someDurationSinceFirstInstant = someDiffFromStart))
    }
    logEntries.setAll(tempList)
    // activate listener again
    chunkListView.addInvalidationListener()
    // update slider boundaries
    timeOpsToolBar.updateSliderBoundaries()
    closeStage
  })


}
