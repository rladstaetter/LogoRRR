package app.logorrr.conf

import app.logorrr.OsxBridge
import app.logorrr.conf.SettingsIO.renderOptions
import app.logorrr.conf.mut.{MutLogFileSettings, MutSettings}
import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.model.LogFileSettings
import app.logorrr.util.{CanLog, OsUtil}
import javafx.application.HostServices
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.Window
import pureconfig.ConfigWriter

import java.nio.file.Path

/**
 * Place LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals extends CanLog {

  val mutSettings = new MutSettings

  private val hostServicesProperty = new SimpleObjectProperty[HostServices]()

  def persist(): Unit = {
    Fs.write(FilePaths.settingsFilePath, ConfigWriter[Settings].to(LogoRRRGlobals.getSettings).render(renderOptions))
  }

  def getOrderedLogFileSettings: Seq[LogFileSettings] = mutSettings.getOrderedLogFileSettings

  def bindWindow(window: Window): Unit = {
    window.setX(LogoRRRGlobals.getStageX)
    window.setY(LogoRRRGlobals.getStageY)
    window.setWidth(LogoRRRGlobals.getStageWidth)
    window.setHeight(LogoRRRGlobals.getStageHeight)

    mutSettings.bindWindowProperties(window)
  }

  def unbindWindow(): Unit = mutSettings.unbindWindow()

  def getStageWidth: Int = mutSettings.getStageWidth

  def getStageHeight: Int = mutSettings.getStageHeight

  def getStageX: Double = mutSettings.getStageX

  def getStageY: Double = mutSettings.getStageY

  def setHostServices(hostServices: HostServices): Unit = hostServicesProperty.set(hostServices)

  def getHostServices: HostServices = hostServicesProperty.get()

  def set(settings: Settings, hostServices: HostServices): Unit = {
    mutSettings.set(settings)
    setHostServices(hostServices)
  }

  def getSettings: Settings = mutSettings.petrify()

  def setSomeActive(sActive: Option[String]): Unit = mutSettings.setSomeActive(sActive)

  def getSomeActive: Option[String] = mutSettings.getSomeActive

  def getSomeLastUsedDirectory: Option[Path] = mutSettings.getSomeLastUsedDirectory

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = mutSettings.setSomeLastUsedDirectory(someDirectory)

  def removeLogFile(pathAsString: String): Unit = {

    mutSettings.removeLogFileSetting(pathAsString)
    mutSettings.setSomeActive(mutSettings.getSomeActive match {
      case Some(value) if value == pathAsString => None
      case x => x
    })

    if (OsUtil.enableSecurityBookmarks) {
      OsxBridge.releasePath(pathAsString)
    }

    logInfo(s"Removed file $pathAsString ...")
  }

  def clearLogFileSettings(): Unit = mutSettings.clearLogFileSettings()

  def getLogFileSettings(pathAsString: String): MutLogFileSettings = {
    mutSettings.getMutLogFileSetting(pathAsString)
  }

  def mupdate(t: MutLogFileSettings => Unit)(pathAsString: String): Unit =
    Option(mutSettings.getMutLogFileSetting(pathAsString)) match {
      case Some(logFileSettings) => t(logFileSettings)
      case None => logWarn(s"$pathAsString not found.")
    }


  def setBlockSettings(pathAsString: String, bs: BlockSettings): Unit =
    mupdate({ lfs: MutLogFileSettings => lfs.setBlockSettings(bs) })(pathAsString)

  def setDividerPosition(pathAsString: String, dividerPosition: Double): Unit = mutSettings.getMutLogFileSetting(pathAsString).setDividerPosition(dividerPosition)

  def updateLogFile(fs: LogFileSettings): Unit = mutSettings.putMutLogFileSetting(MutLogFileSettings(fs))

  def logVisualCanvasWidth(pathAsString: String): Int = (mutSettings.getStageWidth * LogoRRRGlobals.getLogFileSettings(pathAsString).getDividerPosition()).intValue

}
