package app.logorrr.views.settings.timer

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.{CanLog, HLink}
import javafx.beans.binding.{Bindings, StringBinding}
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, VBox}


object TimerSettingsBorderPane {

  val dateTimeFormatterLink = HLink("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html", "format description")

  def mkTf(name: String
           , somePrompt: Option[String]
           , someDefault: Option[String]
           , columnCount: Int): (Label, TextField) = {
    val l = new Label(name)
    val tf = new TextField()
    someDefault.foreach(df => tf.setText(df))
    somePrompt.foreach(pt => tf.setPromptText(pt))
    tf.setPrefColumnCount(columnCount)
    (l, tf)
  }
}

class TimerSettingsBorderPane(settings: MutLogFileSettings
                              , logEntriesToDisplay: ObservableList[LogEntry]
                              , updateLogEntrySetting: LogEntryInstantFormat => Unit
                              , closeStage: => Unit)
  extends BorderPane with CanLog {

  /*
   * those properties exist since it is easier to use from the call sites.
   **/
  private val (startColProperty, endColProperty) = settings.someLogEntrySettingsProperty.get() match {
    case Some(value) => (new SimpleObjectProperty[java.lang.Integer](value.startCol), new SimpleObjectProperty[java.lang.Integer](value.endCol))
    case None => (new SimpleObjectProperty[java.lang.Integer](), new SimpleObjectProperty[java.lang.Integer]())
  }

  private val rangeTextBinding = Bindings.createStringBinding(() => {
    (Option(getStartCol()), Option(getEndCol())) match {
      case (Some(start), Some(end)) => s"Range: (${start.toString}/${end.toString})"
      case (Some(start), None) => s"Range: (${start.toString}/not set)"
      case (None, Some(end)) => s"select start col: (not set/${end.toString})"
      case (None, None) => "Select range."
    }

  }, startColProperty, endColProperty)

  private val rangeColLabel = {
    val l = new Label()
    l.textProperty().bind(rangeTextBinding)
    l
  }

  private val selectedRangeLabel = new Label("Selected: ")
  private val (timeFormatLabel, timeFormatTf) = TimerSettingsBorderPane.mkTf("time format", Option("<enter time format>"), Option(LogEntryInstantFormat.DefaultPattern), 30)

  private val timerSettingsLogTextView = {
    val tslv = new TimerSettingsLogView(settings, logEntriesToDisplay)
    startColProperty.bind(tslv.startColProperty)
    endColProperty.bind(tslv.endColProperty)
    tslv
  }

  /**
   * if ok button is clicked, log definition will be written, settings stage will be closed, associated logfile
   * definition will be updated
   * */
  private val okButton = {
    val b = new Button("set new format")
    b.setOnAction(_ => {
      updateLogEntrySetting(LogEntryInstantFormat(SimpleRange(getStartCol(), getEndCol()), timeFormatTf.getText.trim))
      closeStage
    })
    b
  }
  private val stringBinding = new StringBinding {
    bind(startColProperty, endColProperty)

    override def computeValue(): String = {
      ""
    }
  }
  private val hyperlink: Hyperlink = TimerSettingsBorderPane.dateTimeFormatterLink.mkHyperLink()
  private val selectedBar = new ToolBar(selectedRangeLabel)
  private val timeFormatBar = new ToolBar(timeFormatLabel, timeFormatTf, hyperlink)
  private val bar = new ToolBar(rangeColLabel, okButton)
  private val vbox = new VBox(selectedBar, timeFormatBar, bar)

  init()

  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol(): java.lang.Integer = startColProperty.get()

  def getEndCol(): java.lang.Integer = endColProperty.get()


  def init(): Unit = {
    settings.someLogEntrySettingsProperty.get() match {
      case Some(s) =>
        timeFormatTf.setText(s.dateTimePattern)
      case None =>
        logTrace("No time setting found ... ")
    }

    setCenter(timerSettingsLogTextView)
    setBottom(vbox)

  }
}