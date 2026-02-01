package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.LogoRRRGlobals
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
                            owner: Window,
                            chunkListView: ChunkListView[LogEntry],
                            logEntries: ObservableList[LogEntry],
                            tsRegion: TimestampSettingsRegion) extends Stage:

  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle(s"Specifiy timestamp pattern and position")
  private val timeStampSettingsBorderPane =
    new TimestampSettingsBorderPane(settings
      , logEntries
      , chunkListView
      , tsRegion
      , JfxUtils.closeStage(this)) {
      init(LogoRRRGlobals.getTimestampSettings.map(_.mkImmutable()), settings.getSomeTimestampSettings)
    }


  val scene = new Scene(
    timeStampSettingsBorderPane
    , TimestampSettingStage.width
    , TimestampSettingStage.height)


  setScene(scene)

  setOnCloseRequest:
    _ =>
      unbind()
      this.close()

  def unbind(): Unit = timeStampSettingsBorderPane.unbind()