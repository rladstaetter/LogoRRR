package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.OpsToolBar
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.{Modality, Stage, Window}

object TimestampSettingStage {

  val width = 1100
  val height = 300

}

class TimestampSettingStage(owner: Window
                            , settings: MutLogFileSettings
                            , chunkListView: ChunkListView[LogEntry]
                            , logEntries: ObservableList[LogEntry]
                            , opsToolBar: OpsToolBar) extends Stage {

  initOwner(owner)
  initModality(Modality.APPLICATION_MODAL)
  setTitle(s"Specify timestamp range (from - to columns) and time pattern for ${settings.getFileId.fileName}")

  val scene = new Scene(
    new TimestampSettingsBorderPane(settings
      , logEntries
      , chunkListView
      , opsToolBar
      , JfxUtils.closeStage(this))
    , TimestampSettingStage.width
    , TimestampSettingStage.height)

  setScene(scene)
  setOnCloseRequest(_ => this.close())
}
