package app.logorrr.views.ops.time

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, TimestampSettings}
import app.logorrr.model.{BoundFileId, LogEntry}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.search.TimestampSettingsRegion
import app.logorrr.views.settings.timestamp.TimestampSettingStage
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectPropertyBase
import javafx.collections.ObservableList
import javafx.scene.layout.StackPane
import javafx.stage.Window
import org.kordamp.ikonli.javafx.FontIcon

object TimestampSettingsButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampSettingsButton])


class ExclamationCircleFontIcon extends FontIcon:
  setStyle("-fx-icon-code:fas-exclamation-circle;-fx-icon-color:rgba(255, 0, 0, 1);-fx-icon-size:8;")
  setTranslateX(10)
  setTranslateY(-10)

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
                              , tsRegion: TimestampSettingsRegion)
  extends StackPane with BoundFileId(TimestampSettingsButton.uiNode(_).value):

  private val button: ClockButton = new ClockButton(new TimestampSettingStage(settings, chunkListView, logEntries, tsRegion))
  private val fontIcon = new ExclamationCircleFontIcon

  getChildren.addAll(button, fontIcon)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , hasTimestampSetting: BooleanBinding
           , someGlobalTimestampSettings: Option[TimestampSettings]
           , someLocalTimestampSettings: Option[TimestampSettings]
           , owner: Window): Unit =
    button.init(someGlobalTimestampSettings, someLocalTimestampSettings, owner)
    bindIdProperty(fileIdProperty)
    fontIcon.visibleProperty().bind(hasTimestampSetting.not())

  def unbind(): Unit =
    button.shutdown()
    unbindIdProperty()
    fontIcon.visibleProperty().unbind()
