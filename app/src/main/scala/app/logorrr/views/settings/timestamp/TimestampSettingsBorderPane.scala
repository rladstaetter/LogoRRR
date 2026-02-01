package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{LogoRRRGlobals, TimestampSettings}
import app.logorrr.model.{BoundFileId, LogEntry}
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.beans.binding.Bindings
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.*
import javafx.scene.layout.{BorderPane, HBox, VBox}
import net.ladstatt.util.log.TinyLog


class TimestampSettingsBorderPane(mutLogFileSettings: MutLogFileSettings
                                  , logEntries: ObservableList[LogEntry]
                                  , chunkListView: ChunkListView[LogEntry]
                                  , tsRegion: TimestampSettingsRegion
                                  , closeStage: => Unit)
  extends BorderPane with TinyLog:

  private val startColLabel = new FromLabel
  private val startColTf = new FromTextField

  private val endColLabel = new ToLabel
  private val endColTf = new ToTextField

  private val boundFileIdControls: Seq[? <: BoundFileId] =
    Seq(startColLabel, startColTf, endColLabel, endColTf)

  private def getSomeRange: Option[(Int, Int)] = {
    (getStartCol, getEndCol) match
      case (a, b) if a <= b => Option((a, b))
      case _ => None
  }

  private val startColProperty = new SimpleIntegerProperty()
  private val endColProperty = new SimpleIntegerProperty()
  private val timeFormatTf = new TimeFormatTextField(mutLogFileSettings.getFileId, "")

  startColTf.textProperty().bind(Bindings.createStringBinding(() => {
    Option(getStartCol) match {
      case Some(value) => value.toString
      case None => ""
    }
  }, startColProperty))

  endColTf.textProperty().bind(Bindings.createStringBinding(() => {
    Option(getEndCol) match {
      case Some(value) => value.toString
      case None => ""
    }
  }, endColProperty))


  private val resetTimestampFormatButton = new TimestampFormatResetButton(mutLogFileSettings, chunkListView, logEntries, tsRegion, closeStage)
  private val applyAndCloseButton = new TimestampFormatSetButton(mutLogFileSettings, getSomeRange, timeFormatTf, chunkListView, logEntries, tsRegion, closeStage)

  private val hyperlink: Hyperlink =
    val hl = TimestampSettings.dateFormatterHLink.mkHyperLink()
    hl.setAlignment(Pos.CENTER)
    hl.setPrefWidth(150)
    hl

  private val ShowMax = 26 // how many rows should be shown in the TimeStamp Settings Dialog at max
  private val showThisManyRows = if logEntries.size() > ShowMax then ShowMax else logEntries.size()
  private val firstVisible = Option(mutLogFileSettings.firstVisibleTextCellIndexProperty.get()).getOrElse(0)
  private val lastVisible = Option(mutLogFileSettings.lastVisibleTextCellIndexProperty.get()).getOrElse(logEntries.size())
  private val l: ObservableList[LogEntry] =
    if firstVisible == lastVisible && lastVisible <= 0 || firstVisible > lastVisible then // filter out nonsensical values
      val entries = for i <- 0 until showThisManyRows yield logEntries.get(i)
      FXCollections.observableArrayList(entries *)
    else
      val entries = for i <- firstVisible until lastVisible yield logEntries.get(i)
      FXCollections.observableArrayList(entries *)

  private val timerSettingsLogTextView =
    val tslv = new TimestampPositionSelectionBorderPane(mutLogFileSettings, l)
    startColProperty.bind(tslv.startColProperty)
    endColProperty.bind(tslv.endColProperty)
    tslv

  private val spacer = new AlwaysGrowHorizontalRegion

  private val timeFormatBar = new ToolBar(hyperlink, timeFormatTf, spacer, applyAndCloseButton, resetTimestampFormatButton)

  def updateSettings(settings: TimestampSettings): Unit = {
    timerSettingsLogTextView.startColProperty.set(settings.startCol)
    timerSettingsLogTextView.endColProperty.set(settings.endCol)
    timeFormatTf.setText(settings.dateTimePattern)
  }

  private def initSettings(globalSettings: Option[TimestampSettings]
                           , localSettings: Option[TimestampSettings]): Unit = {
    (globalSettings, localSettings) match {
      case (_, Some(s)) => updateSettings(s)
      case (Some(s), _) => updateSettings(s)
      case _ =>
    }
  }


  def init(globalSettings: Option[TimestampSettings]
           , localSettings: Option[TimestampSettings]): Unit =
    initBindings()
    initSettings(globalSettings, localSettings)
    // Left Sidebar for Column Inputs (The "Selection" phase)
    val selectionSettings = new VBox(15)
    selectionSettings.setPadding(new Insets(15))
    selectionSettings.setPrefWidth(200)
    selectionSettings.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;")

    val rangeLabel = new Label("1. Define Column Range")
    rangeLabel.setStyle("-fx-font-weight: bold;")

    val fromBox = new VBox(5, startColLabel, startColTf)
    val toBox = new VBox(5, endColLabel, endColTf)

    val description = new Label("Click on the preview text\nto select columns.")
    selectionSettings.getChildren.addAll(rangeLabel, fromBox, toBox, description)
    setLeft(selectionSettings)

    // 3. Center stays as the Visual Selection view
    setCenter(timerSettingsLogTextView)

    // 4. Bottom for Format and Actions (The "Finalization" phase)
    val footer = new VBox(10)
    footer.setPadding(new Insets(15))
    footer.setStyle("-fx-background-color: #eee; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;")

    val formatLabel = new Label("2. Define Time Pattern")
    formatLabel.setStyle("-fx-font-weight: bold;")

    val formatInputRow = new HBox(10)
    formatInputRow.setAlignment(Pos.CENTER_LEFT)
    // Make TextField expand to fill space
    HBox.setHgrow(timeFormatTf, javafx.scene.layout.Priority.ALWAYS)
    timeFormatTf.setPromptText("e.g. yyyy-MM-dd HH:mm:ss.SSS")

    formatInputRow.getChildren.addAll(timeFormatTf, hyperlink)

    val actionRow = new HBox(10)
    actionRow.setAlignment(Pos.CENTER_RIGHT)
    actionRow.getChildren.addAll(resetTimestampFormatButton, spacer, applyAndCloseButton)

    footer.getChildren.addAll(formatLabel, formatInputRow, actionRow)
    setBottom(footer)
    timerSettingsLogTextView.listView.requestFocus()


  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol: java.lang.Integer = startColProperty.get()

  def getEndCol: java.lang.Integer = endColProperty.get()

  def initBindings(): Unit =
    boundFileIdControls.foreach(_.bindIdProperty(mutLogFileSettings.fileIdProperty))

  def unbind(): Unit = boundFileIdControls.foreach(_.unbindIdProperty())


