package app.logorrr.views.ops.time

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.search.TimestampSettingsRegion
import app.logorrr.views.settings.timestamp.TimestampSettingStage
import app.logorrr.views.util.GfxElements
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import javafx.stage.Window

class ClockButton(owner: Window
                  , mutLogFileSettings: MutLogFileSettings
                  , chunkListView: ChunkListView[LogEntry]
                  , logEntries: ObservableList[LogEntry]
                  , tsRegion: TimestampSettingsRegion) extends Button:
  setBackground(null)
  setStyle(
    """|-color-button-bg: -color-bg-subtle;
       |-fx-background-insets: 0;
       |""".stripMargin)

  setGraphic(GfxElements.Icons.clock)
  setTooltip(new Tooltip("configure time format"))
  setOnAction(_ => {
    val stage = new TimestampSettingStage(mutLogFileSettings, chunkListView, logEntries, tsRegion)
    stage.init(owner)
    stage.showAndWait()
  })

