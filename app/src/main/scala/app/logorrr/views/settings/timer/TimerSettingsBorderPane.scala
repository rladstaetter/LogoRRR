package app.logorrr.views.settings.timer

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{LogEntry, LogEntryInstantFormat}
import app.logorrr.util.{CanLog, HLink}
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control._
import javafx.scene.layout.BorderPane


object TimerSettingsBorderPane {

  val dateTimeFormatterLink = HLink("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html", "format description")

  def mkTf(name: String
           , somePrompt: Option[String]
           , columnCount: Int): (Label, TextField) = {
    val l = new Label(name)
    val tf = new TextField()
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
  val startColProperty = new SimpleObjectProperty[java.lang.Integer]()
  val endColProperty = new SimpleObjectProperty[java.lang.Integer]()

  lazy val rangeTextBinding = Bindings.createStringBinding(() => {
    (Option(getStartCol()), Option(getEndCol())) match {
      case (Some(start), Some(end)) => s"Range: (${start.toString}/${end.toString})"
      case (Some(end), None) => "Range: n/a"
      case (None, Some(end)) => "Range: n/a"
      case (None, None) => "Range: n/a"
    }

  }, startColProperty, endColProperty)

  val rangeColLabel = {
    val l = new Label()
    l.textProperty().bind(rangeTextBinding)
    l
  }

  val (timeFormatLabel, timeFormatTf) = TimerSettingsBorderPane.mkTf("time format", Option("<enter time format>"), 30)

  val timerSettingsLogTextView = {
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
      updateLogEntrySetting(mkLogEntrySetting)
      closeStage
    })
    b
  }

  private val hyperlink: Hyperlink = TimerSettingsBorderPane.dateTimeFormatterLink.mkHyperLink()

  private val bar = {
    val tb = new ToolBar()
    tb.getItems.addAll(
      rangeColLabel, timeFormatLabel, timeFormatTf, hyperlink
      , okButton)
    tb
  }

  init()

  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol(): java.lang.Integer = startColProperty.get()

  def getEndCol(): java.lang.Integer = endColProperty.get()

  def mkLogEntrySetting: LogEntryInstantFormat = {
    val timeFormat = timeFormatTf.getText.trim
    LogEntryInstantFormat(SimpleRange(getStartCol(), getEndCol()), timeFormat)
  }


  def init(): Unit = {
    settings.someLogEntrySettingsProperty.get() match {
      case Some(s) =>
        setStartCol(s.startCol)
        setEndCol(s.endCol)
        //      startColTf.setText(s.startColumn.start.toString)
        //      endColTf.setText(s.startColumn.end.toString)
        timeFormatTf.setText(s.dateTimePattern)
      case None =>
        logTrace("No time setting found ... ")
    }

    setCenter(timerSettingsLogTextView)
    setBottom(bar)

  }
}