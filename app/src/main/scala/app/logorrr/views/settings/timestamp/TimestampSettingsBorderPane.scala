package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{LogoRRRGlobals, TimestampSettings}
import app.logorrr.model.LogEntry
import app.logorrr.views.search.{OpsToolBar, TimestampSettingsRegion}
import javafx.beans.binding.{Bindings, ObjectBinding}
import javafx.beans.property.SimpleObjectProperty
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

  private val startColLabel = new FromLabel(mutLogFileSettings.getFileId)
  private val startColTextField = new FromTextField(mutLogFileSettings.getFileId)

  private val endColLabel = new ToLabel(mutLogFileSettings.getFileId)
  private val endColTextField = new ToTextField(mutLogFileSettings.getFileId)

  def getSomeRange: Option[(Int, Int)] = {
    (getStartCol, getEndCol) match
      case (a, b) if a <= b => Option((a, b))
      case _ => None
  }

  /*
   * those properties exist since it is easier to use from the call sites.
   **/
  private val (startColProperty, endColProperty) =
    mutLogFileSettings.getSomeTimestampSettings match
      case Some(value) => (new SimpleObjectProperty[java.lang.Integer](value.startCol), new SimpleObjectProperty[java.lang.Integer](value.endCol))
      case None =>
        LogoRRRGlobals.getTimestampSettings match
          case Some(globalSettings) =>
            (new SimpleObjectProperty[java.lang.Integer](globalSettings.getStartCol), new SimpleObjectProperty[java.lang.Integer](globalSettings.getEndCol))
          case None =>
            (new SimpleObjectProperty[java.lang.Integer](), new SimpleObjectProperty[java.lang.Integer]())


  startColTextField.textProperty().bind(Bindings.createStringBinding(() => {
    Option(getStartCol) match {
      case Some(value) => value.toString
      case None => ""
    }
  }, startColProperty))

  endColTextField.textProperty().bind(Bindings.createStringBinding(() => {
    Option(getEndCol) match {
      case Some(value) => value.toString
      case None => ""
    }
  }, endColProperty))

  private val timeFormatTf =
    mutLogFileSettings.getSomeTimestampSettings match {
      case Some(value) => new TimeFormatTextField(mutLogFileSettings.getFileId, value.dateTimePattern)
      case None => LogoRRRGlobals.getTimestampSettings match {
        case Some(value) => new TimeFormatTextField(mutLogFileSettings.getFileId, value.getDateTimePatternCol)
        case None => new TimeFormatTextField(mutLogFileSettings.getFileId, "")
      }
    }

  private val setTimestampFormatButton = new TimestampFormatSetButton(mutLogFileSettings, getSomeRange, timeFormatTf, chunkListView, logEntries, tsRegion, closeStage)
  private val resetTimestampFormatButton = new TimestampFormatResetButton(mutLogFileSettings, chunkListView, logEntries, tsRegion, closeStage)

  // binding is just here to trigger refresh on timerSettingsLogTextView
  // has to be assigned to a val otherwise this won't get executed
/*
  val binding: ObjectBinding[String] =
    Bindings.createObjectBinding(() => s"$getStartCol $getEndCol", startColProperty, endColProperty)
  binding.addListener((_, _, _) => {
    timerSettingsLogTextView.listView.refresh()
  })*/

  private val hyperlink: Hyperlink =
    val hl = TimestampSettings.dateFormatterHLink.mkHyperLink()
    hl.setAlignment(Pos.CENTER)
    hl.setPrefWidth(150)
    hl

  private val ShowMax = 26 // how many rows should be shown in the TimeStamp Settings Dialog at max
  private lazy val showThisManyRows = if logEntries.size() > ShowMax then ShowMax else logEntries.size()
  private val firstVisible = Option(mutLogFileSettings.firstVisibleTextCellIndexProperty.get()).getOrElse(0)
  private val lastVisible = Option(mutLogFileSettings.lastVisibleTextCellIndexProperty.get()).getOrElse(logEntries.size())
  private val l =
    if firstVisible == lastVisible && lastVisible == 0 then // if first/last visible was not yet set
      FXCollections.observableArrayList((for i <- firstVisible until showThisManyRows yield logEntries.get(i)) *)
    else
      FXCollections.observableArrayList((for i <- firstVisible until lastVisible yield logEntries.get(i)) *)

  private val timerSettingsLogTextView =
    val tslv = new TimestampPositionSelectionBorderPane(mutLogFileSettings, l)
    startColProperty.bind(tslv.startColProperty)
    endColProperty.bind(tslv.endColProperty)
    tslv

  private val spacer = new AlwaysGrowHorizontalRegion

  private val timeFormatBar = new ToolBar(hyperlink, timeFormatTf, spacer, setTimestampFormatButton, resetTimestampFormatButton)

  newInit()

  def newInit(): Unit =
    // Left Sidebar for Column Inputs (The "Selection" phase)
    val selectionSettings = new VBox(15)
    selectionSettings.setPadding(new Insets(15))
    selectionSettings.setPrefWidth(200)
    selectionSettings.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;")

    val rangeLabel = new Label("1. Define Column Range")
    rangeLabel.setStyle("-fx-font-weight: bold;")

    val fromBox = new VBox(5, startColLabel, startColTextField)
    val toBox = new VBox(5, endColLabel, endColTextField)

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
    actionRow.getChildren.addAll(resetTimestampFormatButton, spacer, setTimestampFormatButton)

    footer.getChildren.addAll(formatLabel, formatInputRow, actionRow)
    setBottom(footer)
    timerSettingsLogTextView.listView.requestFocus()



  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol: java.lang.Integer = startColProperty.get()

  def getEndCol: java.lang.Integer = endColProperty.get()




