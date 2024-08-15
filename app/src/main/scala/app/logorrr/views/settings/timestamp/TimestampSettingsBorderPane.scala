package app.logorrr.views.settings.timestamp

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{LogEntry, TimestampSettings}
import app.logorrr.util.{CanLog, HLink, JfxUtils}
import app.logorrr.views.UiNodes
import app.logorrr.views.block.ChunkListView
import app.logorrr.views.ops.time.TimeOpsToolBar
import javafx.beans.binding.{Bindings, ObjectBinding}
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.{Insets, Pos}
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, HBox, Region, VBox}

import java.time.{Duration, Instant}
import java.util


object TimestampSettingsBorderPane {


}

class TimestampSettingsBorderPane(mutLogFileSettings: MutLogFileSettings
                                  , logEntries: ObservableList[LogEntry]
                                  , chunkListView: ChunkListView
                                  , timeOpsToolBar: TimeOpsToolBar
                                  , closeStage: => Unit)
  extends BorderPane with CanLog {

  val labelWidth = 100
  val textFieldWidth = 60

  private val fromTextField = {
    val tf = JfxUtils.mkTextField(textFieldWidth)
    tf.setEditable(false)
    tf
  }

  private val toTextField = {
    val tf = JfxUtils.mkTextField(textFieldWidth)
    tf.setEditable(false)
    tf
  }

  private val fromLabel = JfxUtils.mkL("from column:", labelWidth)
  private val toLabel = JfxUtils.mkL("to column:", labelWidth)


  /*
   * those properties exist since it is easier to use from the call sites.
   **/
  private val (startColProperty, endColProperty) = mutLogFileSettings.getSomeTimestampSettings match {
    case Some(value) => (new SimpleObjectProperty[java.lang.Integer](value.startCol), new SimpleObjectProperty[java.lang.Integer](value.endCol))
    case None => (new SimpleObjectProperty[java.lang.Integer](), new SimpleObjectProperty[java.lang.Integer]())
  }

  fromTextField.textProperty().bind(Bindings.createStringBinding(() => {
    Option(getStartCol) match {
      case Some(value) => value.toString
      case None => ""
    }
  }, startColProperty))

  toTextField.textProperty().bind(Bindings.createStringBinding(() => {
    Option(getEndCol) match {
      case Some(value) => value.toString
      case None => ""
    }
  }, endColProperty))


  private val resetButton = {
    val b = new Button("reset")
    b.setAlignment(Pos.CENTER_RIGHT)
    b.setPrefWidth(180)
    b.setOnAction(_ => {
      mutLogFileSettings.setSomeLogEntryInstantFormat(None)
      LogoRRRGlobals.persist()
      // we have to deactivate this listener otherwise
      chunkListView.removeInvalidationListener()
      val tempList = new util.ArrayList[LogEntry]()
      logEntries.forEach(e => {
        tempList.add(e.withOutTimestamp())
      })
      logEntries.setAll(tempList)
      // activate listener again
      chunkListView.addInvalidationListener()
      timeOpsToolBar.updateSliderBoundaries()
      closeStage
    })
    b
  }

  /**
   * if ok button is clicked, log definition will be written, settings stage will be closed, associated logfile
   * definition will be updated
   * */
  private val okButton = {
    val b = new Button("set format")
    b.setPrefWidth(400)
    b.setAlignment(Pos.CENTER)
    b.setOnAction(_ => {
      val leif: TimestampSettings = TimestampSettings(SimpleRange(getStartCol, getEndCol), timeFormatTf.getText.trim)
      mutLogFileSettings.setSomeLogEntryInstantFormat(Option(leif))
      LogoRRRGlobals.persist()
      // we have to deactivate this listener otherwise
      chunkListView.removeInvalidationListener()
      var someFirstEntryTimestamp: Option[Instant] = None

      val tempList = new util.ArrayList[LogEntry]()
      for (i <- 0 until logEntries.size()) {
        val e = logEntries.get(i)
        val someInstant = TimestampSettings.parseInstant(e.value, leif)
        if (someFirstEntryTimestamp.isEmpty) {
          someFirstEntryTimestamp = someInstant
        }

        val diffFromStart: Option[Duration] = for {
          firstEntry <- someFirstEntryTimestamp
          instant <- someInstant
        } yield Duration.between(firstEntry, instant)

        tempList.add(e.copy(someInstant = someInstant, someDurationSinceFirstInstant = diffFromStart))
      }
      logEntries.setAll(tempList)
      // activate listener again
      chunkListView.addInvalidationListener()
      // update slider boundaries
      timeOpsToolBar.updateSliderBoundaries()
      closeStage
    })
    b
  }

  // has to be assigned to a val otherwise this won't get intepreted
  val binding: ObjectBinding[String] =
    Bindings.createObjectBinding(() => s"$getStartCol $getEndCol", startColProperty, endColProperty)

  // if either startCol or endCol is changed, refresh listview
  binding.addListener((_, _, _) => {
    timerSettingsLogTextView.listView.refresh()
  })

  private val hyperlink: Hyperlink = {
    val hl = HLink(UiNodes.OpenDateFormatterSite, "https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/time/format/DateTimeFormatter.html", "time pattern").mkHyperLink()
    hl.setAlignment(Pos.CENTER)
    hl.setPrefWidth(83)
    hl
  }

  private val timeFormatTf = {
    val tf = new TextField()
    tf.setPromptText("<enter time format>")
    tf.setText(TimestampSettings.DefaultPattern)
    tf.setPrefColumnCount(30)
    tf
  }

  private val timerSettingsLogTextView = {
    val tslv = new TimerSettingsLogView(mutLogFileSettings, logEntries)
    startColProperty.bind(tslv.startColProperty)
    endColProperty.bind(tslv.endColProperty)
    tslv
  }

  private val spacer = {
    val s = new Region()
    HBox.setHgrow(s, javafx.scene.layout.Priority.ALWAYS)
    s
  }
  private val timeFormatBar = new ToolBar(hyperlink, timeFormatTf, okButton, spacer, resetButton)

  init()


  def init(): Unit = {
    mutLogFileSettings.getSomeTimestampSettings match {
      case Some(s) => timeFormatTf.setText(s.dateTimePattern)
      case None => logTrace("No time setting found ... ")
    }

    val leftLabel = new Label("select range")
    val leftVBox = new VBox(leftLabel)
    leftVBox.setAlignment(Pos.CENTER); // Center the label vertically
    leftVBox.setPadding(new Insets(10))
    setLeft(leftVBox)

    val fromRow = new HBox(10, fromLabel, fromTextField) // Spacing between label and text field is 10
    fromRow.setAlignment(Pos.CENTER_LEFT)

    val toRow = new HBox(10, toLabel, toTextField) // Spacing between label and text field is 10

    toRow.setAlignment(Pos.CENTER_LEFT)

    val vbox = new VBox(10, fromRow, toRow) // Spacing between rows is 10

    vbox.setAlignment(Pos.CENTER) // Center the elements in the VBox

    vbox.setPadding(new Insets(10)) // Padding around the VBox


    setRight(vbox)

    setCenter(timerSettingsLogTextView)
    setBottom(timeFormatBar)

  }

  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol: java.lang.Integer = startColProperty.get()

  def getEndCol: java.lang.Integer = endColProperty.get()

}