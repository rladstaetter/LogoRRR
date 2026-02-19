package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.{LogoRRRGlobals, TimestampSettings}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.stage.{Modality, Stage, Window}

object TimestampSettingStage:

  val width = 1100
  val height = 600


class TimestampSettingStage(settings: MutLogFileSettings,
                            chunkListView: ChunkListView[LogEntry],
                            logEntries: ObservableList[LogEntry],
                            tsRegion: TimestampSettingsRegion) extends Stage:

  initModality(Modality.WINDOW_MODAL)
  setTitle(s"Specifiy timestamp pattern and position")

  private val timeStampSettingsBorderPane = new TimestampSettingsBorderPane(settings
    , logEntries
    , chunkListView
    , tsRegion
    , JfxUtils.closeStage(this))

  setOnCloseRequest:
    _ =>
      shutdown()
      this.close()

  def init(owner : Window): Unit =
    initOwner(owner)
    timeStampSettingsBorderPane.init(LogoRRRGlobals.getTimestampSettings.map(_.mkImmutable()), settings.getSomeTimestampSettings)
    setScene(new Scene(timeStampSettingsBorderPane, TimestampSettingStage.width, TimestampSettingStage.height))

  def shutdown(): Unit = timeStampSettingsBorderPane.shutdown()