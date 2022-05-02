package app.logorrr.conf

import app.logorrr.conf.SettingsIO.renderOptions
import app.logorrr.conf.mut.{MutLogFileSettings, MutSettings}
import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.model.LogFileSettings
import app.logorrr.util.CanLog
import javafx.application.HostServices
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.Window
import pureconfig.ConfigWriter

import scala.jdk.CollectionConverters._

/**
 * Place LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals extends CanLog {

  def persist(): Unit = write(LogoRRRGlobals.getSettings())

  def allLogs(): Seq[LogFileSettings] = {
    settings.logFileSettingsProperty.get().values.asScala.toSeq.sortWith((lt, gt) => lt.getFirstOpened() < gt.getFirstOpened()).map(_.petrify())
  }


  def bindWindow(window: Window): Unit = {
    window.setX(LogoRRRGlobals.getStageX())
    window.setY(LogoRRRGlobals.getStageY())
    window.setWidth(LogoRRRGlobals.getStageWidth())
    window.setHeight(LogoRRRGlobals.getStageHeight())

    settings.stageSettings.widthProperty.bind(window.getScene.widthProperty())
    settings.stageSettings.heightProperty.bind(window.getScene.heightProperty())
    settings.stageSettings.xProperty.bind(window.xProperty())
    settings.stageSettings.yProperty.bind(window.yProperty())
  }

  def unbindWindow(): Unit = {
    settings.stageSettings.widthProperty.unbind()
    settings.stageSettings.heightProperty.unbind()
    settings.stageSettings.xProperty.unbind()
    settings.stageSettings.yProperty.unbind()
  }

  def getStageWidth(): Int = settings.stageSettings.widthProperty.get()

  def getStageHeight(): Int = settings.stageSettings.heightProperty.get()

  def getStageX(): Double = settings.stageSettings.xProperty.get()

  def getStageY(): Double = settings.stageSettings.yProperty.get()

  val settings = new MutSettings
  private val hostServicesProperty = new SimpleObjectProperty[HostServices]()

  def setHostServices(hostServices: HostServices): Unit = hostServicesProperty.set(hostServices)

  def getHostServices: HostServices = hostServicesProperty.get()

  def set(settings: Settings, hostServices: HostServices): Unit = {
    this.settings.set(settings)
    setHostServices(hostServices)
  }

  def getSettings(): Settings = settings.petrify()

  /** persists settings */
  def write(settings: Settings): Unit = {
    Fs.write(FilePaths.settingsFilePath, ConfigWriter[Settings].to(settings).render(renderOptions))
  }

  private def update(fn: Settings => Settings): Unit = write(fn(LogoRRRGlobals.getSettings()))

  def setSomeActive(sActive: Option[String]): Unit = settings.setSomeActive(sActive)

  def getSomeActive(): Option[String] = settings.getSomeActive()


  def removeLogFile(pathAsString: String): Unit = {
    settings.removeLogFileSetting(pathAsString)
    settings.setSomeActive(settings.getSomeActive() match {
      case Some(value) if value == pathAsString => None
      case x => x
    })
    logInfo(s"Removed file ${pathAsString} ...")
  }

  def resetLogs(): Unit = {
    settings.logFileSettingsProperty.clear()
    settings.setSomeActive(None)
  }

  def getLogFileSettings(pathAsString: String): MutLogFileSettings = {
    settings.getLogFileSetting(pathAsString)
  }

  def mupdate(t: MutLogFileSettings => Unit)(pathAsString: String): Unit =
    Option(settings.getLogFileSetting(pathAsString)) match {
      case Some(logFileSettings) => t(logFileSettings)
      case None => logWarn(s"${pathAsString} not found.")
    }

  def kupdate(t: StageSettings => Unit)(pathAsString: String): Unit =
    Option(settings.getStageSettings()) match {
      case Some(stageSettings: StageSettings) => t(stageSettings)
      case None => logWarn(s"${pathAsString} not found.")
    }

  def updateBlockSettings(pathAsString: String, bs: BlockSettings): Unit =
    mupdate({ lfs: MutLogFileSettings => lfs.setBlockSettings(bs) })(pathAsString)

  def updateDividerPosition(pathAsString: String, dividerPosition: Double): Unit = {
    settings.getLogFileSetting(pathAsString).setDividerPosition(dividerPosition)
    //  mupdate({ lfs: MutLogFileSettings => lfs.setDividerPosition(dividerPosition) })(pathAsString)
  }

  def updateLogFile(fs: LogFileSettings): Unit = {
    settings.putLogFileSetting(fs)
  }


}
