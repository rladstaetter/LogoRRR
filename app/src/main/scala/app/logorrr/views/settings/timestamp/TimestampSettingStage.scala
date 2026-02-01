package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.{OpsToolBar, TimestampSettingsRegion}
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.{Modality, Stage, Window}

object TimestampSettingStage:

  val width = 1100
  val height = 600


class TimestampSettingStage(owner: Window
                            , settings: MutLogFileSettings
                            , chunkListView: ChunkListView[LogEntry]
                            , logEntries: ObservableList[LogEntry]
                            , tsRegion: TimestampSettingsRegion) extends Stage:

  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle(s"Specifiy timestamp pattern and position")

  private val timeStampSettingsBorderPane =
    new TimestampSettingsBorderPane(settings
      , logEntries
      , chunkListView
      , tsRegion
      , JfxUtils.closeStage(this))

  val scene = new Scene(
    timeStampSettingsBorderPane
    , TimestampSettingStage.width
    , TimestampSettingStage.height)

  setScene(scene)
  setOnCloseRequest(_ => this.close())
