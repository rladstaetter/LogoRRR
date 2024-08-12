package app.logorrr.views.settings.timestamp

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.block.ChunkListView
import app.logorrr.views.ops.time.TimeOpsToolBar
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.{Modality, Stage}

object TimestampSettingStage {

  val width = 1100
  val height = 300

}

class TimestampSettingStage(settings: MutLogFileSettings
                            , chunkListView: ChunkListView
                            , logEntries: ObservableList[LogEntry]
                            , timeOpsToolBar: TimeOpsToolBar) extends Stage {
  private val timerSettingsBorderPane = new TimestampSettingsBorderPane(settings
    , logEntries
    , chunkListView
    , timeOpsToolBar
    , JfxUtils.closeStage(this))
  val scene = new Scene(timerSettingsBorderPane, TimestampSettingStage.width, TimestampSettingStage.height)
  initModality(Modality.APPLICATION_MODAL)
  setTitle(s"Timestamp settings for ${settings.getFileId.fileName}")
  setScene(scene)
  setOnCloseRequest(_ => this.close())
}
