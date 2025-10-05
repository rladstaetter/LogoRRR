package app.logorrr.views.ops.time

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.settings.timestamp.TimestampSettingStage
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

object TimestampSettingsButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampSettingsButton])

}

/**
 * Displays a clock in the ops tool bar, with a red exclamation mark if there is no setting for the
 * timestamp format for the given log file.
 *
 * Given a time stamp format (for example: YYYY-MM-dd HH:mm:ss.SSS), LogoRRR is able to parse the timestamp
 * and thus has new possibilities to analyse the log file.
 *
 * @param settings   settings for specific log file
 * @param logEntries the list of log entries to display in order to configure a time format
 */
class TimestampSettingsButton(settings: MutLogFileSettings
                              , chunkListView: ChunkListView[LogEntry]
                              , logEntries: ObservableList[LogEntry]
                              , opsToolBar: OpsToolBar) extends StackPane {

  setId(TimestampSettingsButton.uiNode(settings.getFileId).value)

  // since timerbutton is a stackpane, this css commands are necessary to have the same effect as
  // defined in primer-light.css
  val button: Button = {
    val btn = new Button()
    btn.setBackground(null)
    btn.setStyle(
      """
        |-color-button-bg: -color-bg-subtle;
        |-fx-background-insets: 0;
        |""".stripMargin)
    btn.setGraphic(new FontIcon(FontAwesomeRegular.CLOCK))
    btn.setTooltip(new Tooltip("configure time format"))
    btn.setOnAction(_ => new TimestampSettingStage(getScene.getWindow, settings, chunkListView, logEntries, opsToolBar).showAndWait())
    btn
  }

  private val fontIcon = {
    val icon = new FontIcon()
    icon.setStyle("-fx-icon-code:fas-exclamation-circle;-fx-icon-color:rgba(255, 0, 0, 1);-fx-icon-size:8;")
    icon.setTranslateX(10)
    icon.setTranslateY(-10)

    /** red exclamation mark is only visible if there is no timestamp setting for a given log file */
    icon.visibleProperty().bind(settings.hasTimestampSetting.not())
    icon
  }

  getChildren.addAll(button, fontIcon)

}
