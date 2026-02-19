package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.TimestampSettings
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{Hyperlink, Label}
import javafx.scene.layout.{HBox, VBox}

class TimeSettingsFooter(mutLogFileSettings: MutLogFileSettings
                         , logEntries: ObservableList[LogEntry]
                         , chunkListView: ChunkListView[LogEntry]
                         , tsRegion: TimestampSettingsRegion
                         , closeStage: => Unit) extends VBox(10):

  setPadding(new Insets(15))
  setStyle("-fx-background-color: #eee; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;")

  private val hyperlink: Hyperlink =
    val hl = TimestampSettings.dateFormatterHLink.mkHyperLink()
    hl.setAlignment(Pos.CENTER)
    hl.setPrefWidth(150)
    hl

  private val timeFormatTf = new TimeFormatTextField:
    HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS)
    setPromptText("e.g. yyyy-MM-dd HH:mm:ss.SSS")

  private val spacer = new AlwaysGrowHorizontalRegion

  private val rangeProperty = new SimpleObjectProperty[(Int, Int)]()

  private val resetTimestampFormatButton = new TimestampFormatResetButton(mutLogFileSettings, chunkListView, logEntries, tsRegion, closeStage)

  private val applyAndCloseButton = new TimestampFormatSetButton(mutLogFileSettings, rangeProperty, timeFormatTf, chunkListView, logEntries, tsRegion, closeStage)

  private val formatLabel = new Label("2. Define Time Pattern"):
    setStyle("-fx-font-weight: bold;")

  private val formatInputRow = new HBox(10):
    setAlignment(Pos.CENTER_LEFT)
    getChildren.addAll(timeFormatTf, hyperlink)

  private val actionRow = new HBox(10):
    setAlignment(Pos.CENTER_RIGHT)
    getChildren.addAll(resetTimestampFormatButton, spacer, applyAndCloseButton)

  getChildren.addAll(formatLabel, formatInputRow, actionRow)


  def initDateTimePattern(dateTimePattern: String): Unit = {
    timeFormatTf.setText(dateTimePattern)
  }

  def init(startColProperty: SimpleObjectProperty[java.lang.Integer]
           , endColProperty: SimpleObjectProperty[java.lang.Integer]): Unit =
    resetTimestampFormatButton.bindIdProperty(mutLogFileSettings.fileIdProperty)
    timeFormatTf.bindIdProperty(mutLogFileSettings.fileIdProperty)
    applyAndCloseButton.bindIdProperty(mutLogFileSettings.fileIdProperty)


    rangeProperty.bind(Bindings.createObjectBinding(
      () => {
        (Option(startColProperty.get), Option(endColProperty.get)) match
          case (Some(startCol), Some(endCol)) => (startCol, endCol)
          case _ => null
      }
      , startColProperty
      , endColProperty
    ))

  def shutdown(): Unit =
    resetTimestampFormatButton.unbindIdProperty()
    applyAndCloseButton.unbindIdProperty()

    rangeProperty.unbind()
