package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.{MutLogFileSettings, MutTimeSettings}
import app.logorrr.conf.{LogoRRRGlobals, TimeSettings}
import app.logorrr.model.LogEntry
import app.logorrr.views.logfiletab.LogFilePane
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.beans.property.*
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.layout.BorderPane
import net.ladstatt.util.log.TinyLog


class TimestampSettingsBorderPane(logFilePane: LogFilePane
                                  , mutLogFileSettings: MutLogFileSettings
                                  , logEntries: ObservableList[LogEntry]
                                  , chunkListView: ChunkListView[LogEntry]
                                  , tsRegion: TimestampSettingsRegion
                                  , closeStage: => Unit)
  extends BorderPane with TinyLog:

  val sideBar = new SideBar

  private val startColProperty = new SimpleObjectProperty[java.lang.Integer]()
  private val endColProperty = new SimpleObjectProperty[java.lang.Integer]()

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

  /** dialog where start and end column are to be defined via mouseclick */
  private val timerSettingsLogTextView = new TsStartEndColDialog(mutLogFileSettings, l)

  private val footer = new TimeSettingsFooter(logFilePane, mutLogFileSettings, logEntries, chunkListView, tsRegion, closeStage)

  def updateSettings(settings: MutTimeSettings): Unit = {
    timerSettingsLogTextView.startColProperty.set(settings.getStartCol)
    timerSettingsLogTextView.endColProperty.set(settings.getEndCol)
    footer.initDateTimePattern(settings.getDateTimePattern)
  }

  private def initSettings(globalSettings: MutTimeSettings
                           , localSettings: MutTimeSettings): Unit = {
    (globalSettings.validBinding.get(), localSettings.validBinding.get()) match {
      case (_, true) => updateSettings(localSettings)
      case (true, _) => updateSettings(globalSettings)
      case _ =>
    }
  }

  def initBindings(): Unit =
    sideBar.init(mutLogFileSettings.fileIdProperty, startColProperty, endColProperty)
    timerSettingsLogTextView.init(mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.mutTimeSettings
      , LogoRRRGlobals.timeSettings
      , startColProperty
      , endColProperty)
    footer.init(startColProperty, endColProperty)

  def init(globalSettings: MutTimeSettings
           , localSettings: MutTimeSettings): Unit =
    initBindings()
    initSettings(globalSettings, localSettings)
    setLeft(sideBar)
    setCenter(timerSettingsLogTextView)
    setBottom(footer)
    timerSettingsLogTextView.listView.requestFocus()


  def shutdown(): Unit =
    sideBar.shutdown()
    timerSettingsLogTextView.shutdown(startColProperty, endColProperty)
    footer.shutdown()


