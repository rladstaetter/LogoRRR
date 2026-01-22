package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.HLink
import app.logorrr.views.a11y.uinodes.UiNodes
import app.logorrr.views.search.OpsToolBar
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
                                  , opsToolBar: OpsToolBar
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

  private val setTimestampFormatButton = new TimestampFormatSetButton(mutLogFileSettings, getSomeRange, timeFormatTf, chunkListView, logEntries, opsToolBar, closeStage)
  private val resetTimestampFormatButton = new TimestampFormatResetButton(mutLogFileSettings, chunkListView, logEntries, opsToolBar, closeStage)

  // binding is just here to trigger refresh on timerSettingsLogTextView
  // has to be assigned to a val otherwise this won't get executed
  val binding: ObjectBinding[String] =
    Bindings.createObjectBinding(() => s"$getStartCol $getEndCol", startColProperty, endColProperty)

  // if either startCol or endCol is changed, refresh listview
  binding.addListener((_, _, _) => {
    timerSettingsLogTextView.listView.refresh()
  })

  private val hyperlink: Hyperlink =
    val hl = HLink(UiNodes.OpenDateFormatterSite, "https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/time/format/DateTimeFormatter.html", "time pattern").mkHyperLink()
    hl.setAlignment(Pos.CENTER)
    hl.setPrefWidth(83)
    hl

  private val ShowMax = 100 // how many rows should be shown in the TimeStamp Settings Dialog at max
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

  private val timeFormatBar = new ToolBar(hyperlink, timeFormatTf, setTimestampFormatButton, spacer, resetTimestampFormatButton)

  init()

  def init(): Unit =
    val rangeLabel = new Label("select range")
    val leftVBox = new VBox(rangeLabel)
    leftVBox.setAlignment(Pos.CENTER); // Center the label vertically
    leftVBox.setPadding(new Insets(10))
    setLeft(leftVBox)

    val fromRow = new HBox(10, startColLabel, startColTextField) // Spacing between label and text field is 10
    fromRow.setAlignment(Pos.CENTER_LEFT)
    val toRow = new HBox(10, endColLabel, endColTextField) // Spacing between label and text field is 10

    toRow.setAlignment(Pos.CENTER_LEFT)
    val vbox = new VBox(10, fromRow, toRow) // Spacing between rows is 10

    vbox.setAlignment(Pos.CENTER) // Center the elements in the VBox
    vbox.setPadding(new Insets(10)) // Padding around the VBox

    setRight(vbox)
    setCenter(timerSettingsLogTextView)
    setBottom(timeFormatBar)


  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol: java.lang.Integer = startColProperty.get()

  def getEndCol: java.lang.Integer = endColProperty.get()




