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
    persist(LogoRRRGlobals.getSettings)
  }

  def persist(settings: Settings): Unit = {
    Fs.write(FilePaths.settingsFilePath, ConfigWriter[Settings].to(settings).render(renderOptions))
  }

  def getOrderedLogFileSettings: Seq[LogFileSettings] = mutSettings.getOrderedLogFileSettings

  def bindWindow(window: Window): Unit = {
    window.setX(LogoRRRGlobals.getStageX)
    window.setY(LogoRRRGlobals.getStageY)
    window.setWidth(LogoRRRGlobals.getStageWidth)
    window.setHeight(LogoRRRGlobals.getStageHeight)

    mutSettings.bindWindowProperties(window)
  }

  def unbindWindow(): Unit = {
    mutSettings.unbindWindow()
  }

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

  /** a case class representing current setting state */
  def getSettings: Settings = mutSettings.petrify()

  def setSomeActiveLogFile(sActive: Option[String]): Unit = {
    mutSettings.setSomeActive(sActive)
  }

  def getSomeActiveLogFile: Option[String] = mutSettings.getSomeActiveLogFile

  def getSomeLastUsedDirectory: Option[Path] = mutSettings.getSomeLastUsedDirectory

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = mutSettings.setSomeLastUsedDirectory(someDirectory)

  def removeLogFile(pathAsString: String): Unit = timeR({
    mutSettings.removeLogFileSetting(pathAsString)
    mutSettings.setSomeActive(mutSettings.getSomeActiveLogFile match {
      case Some(value) if value == pathAsString => None
      case x => x
    })

    if (OsUtil.enableSecurityBookmarks) {
      OsxBridge.releasePath(pathAsString)
    }

  }, s"Removed file $pathAsString ...")

  def clearLogFileSettings(): Unit = mutSettings.clearLogFileSettings()

  def getLogFileSettings(pathAsString: String): MutLogFileSettings = {
    mutSettings.getMutLogFileSetting(pathAsString)
  }

  def registerSettings(fs: LogFileSettings): Unit = mutSettings.putMutLogFileSetting(MutLogFileSettings(fs))

}
