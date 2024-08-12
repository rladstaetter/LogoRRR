package app.logorrr.views.settings.timestamp

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.{LogEntry, TimestampSettings}
import app.logorrr.util.{CanLog, HLink}
import app.logorrr.views.UiNodes
import app.logorrr.views.block.ChunkListView
import app.logorrr.views.ops.time.TimeOpsToolBar
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.css.PseudoClass
import javafx.scene.control._
import javafx.scene.layout.BorderPane

import java.time.{Duration, Instant}
import java.util


object TimestampSettingsBorderPane {

  private val dateTimeFormatterLink: HLink = HLink(UiNodes.OpenDateFormatterSite, "https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/time/format/DateTimeFormatter.html", "format description")

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

class TimestampSettingsBorderPane(settings: MutLogFileSettings
                                  , logEntries: ObservableList[LogEntry]
                                  , chunkListView: ChunkListView
                                 , timeOpsToolBar: TimeOpsToolBar
                                  , closeStage: => Unit)
  extends BorderPane with CanLog {

  /*
   * those properties exist since it is easier to use from the call sites.
   **/
  private val (startColProperty, endColProperty) = settings.getSomeTimestampSettings match {
    case Some(value) => (new SimpleObjectProperty[java.lang.Integer](value.startCol), new SimpleObjectProperty[java.lang.Integer](value.endCol))
    case None => (new SimpleObjectProperty[java.lang.Integer](), new SimpleObjectProperty[java.lang.Integer]())
  }

  private val resetButton = {
    val b = new Button("reset")
    b.setOnAction(_ => {
      settings.setSomeLogEntryInstantFormat(None)
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

    b.setOnAction(_ => {
      val leif: TimestampSettings = TimestampSettings(SimpleRange(getStartCol, getEndCol), timeFormatTf.getText.trim)
      settings.setSomeLogEntryInstantFormat(Option(leif))
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


  private val rangeTextBinding = Bindings.createStringBinding(() => {
    (Option(getStartCol), Option(getEndCol)) match {
      case (Some(start), Some(end)) =>
        okButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true)
        okButton.requestFocus()
        s"Range: (${start.toString}/${end.toString})"
      case (Some(start), None) => s"Range: (${start.toString}/not set)"
      case (None, Some(end)) => s"select start col: (not set/${end.toString})"
      case (None, None) => "Select range."
    }

  }, startColProperty, endColProperty)

  private val rangeColLabel = {
    val l = new Label()
    l.setPrefWidth(200)
    l.textProperty().bind(rangeTextBinding)
    l
  }

  private val (timeFormatLabel, timeFormatTf) = TimestampSettingsBorderPane.mkTf("time format", Option("<enter time format>"), Option(TimestampSettings.DefaultPattern), 30)

  private val timerSettingsLogTextView = {
    val tslv = new TimerSettingsLogView(settings, logEntries)
    startColProperty.bind(tslv.startColProperty)
    endColProperty.bind(tslv.endColProperty)
    tslv
  }

  private val hyperlink: Hyperlink = TimestampSettingsBorderPane.dateTimeFormatterLink.mkHyperLink()
  private val timeFormatBar = new ToolBar(rangeColLabel, timeFormatLabel, timeFormatTf, hyperlink, okButton, resetButton)

  init()


  def init(): Unit = {
    settings.getSomeTimestampSettings match {
      case Some(s) => timeFormatTf.setText(s.dateTimePattern)
      case None => logTrace("No time setting found ... ")
    }

    setCenter(timerSettingsLogTextView)
    setBottom(timeFormatBar)

  }

  def setStartCol(startCol: Int): Unit = startColProperty.set(startCol)

  def setEndCol(endCol: Int): Unit = endColProperty.set(endCol)

  def getStartCol: java.lang.Integer = startColProperty.get()

  def getEndCol: java.lang.Integer = endColProperty.get()

}