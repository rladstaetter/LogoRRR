package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, TimestampSettings}
import app.logorrr.model.{BoundFileId, IntStringBinding, LogEntry}
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.beans.binding.Bindings
import javafx.beans.property.*
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

  private val footer = new TimeSettingsFooter(mutLogFileSettings, logEntries, chunkListView, tsRegion, closeStage)

  def updateSettings(settings: TimestampSettings): Unit = {
    timerSettingsLogTextView.startColProperty.set(settings.startCol)
    timerSettingsLogTextView.endColProperty.set(settings.endCol)
    footer.initDateTimePattern(settings.dateTimePattern)
  }

  private def initSettings(globalSettings: Option[TimestampSettings]
                           , localSettings: Option[TimestampSettings]): Unit = {
    (globalSettings, localSettings) match {
      case (_, Some(s)) => updateSettings(s)
      case (Some(s), _) => updateSettings(s)
      case _ =>
    }
  }

  def initBindings(): Unit =
    sideBar.init(mutLogFileSettings.fileIdProperty, startColProperty, endColProperty)
    timerSettingsLogTextView.init(mutLogFileSettings.fileIdProperty
      , mutLogFileSettings.getSomeTimestampSettings
      , LogoRRRGlobals.getTimestampSettings.map(_.mkImmutable())
      , startColProperty
      , endColProperty)
    footer.init(startColProperty, endColProperty)

  def init(globalSettings: Option[TimestampSettings]
           , localSettings: Option[TimestampSettings]): Unit =
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


