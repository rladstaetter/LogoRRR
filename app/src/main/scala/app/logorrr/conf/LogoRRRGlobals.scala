package app.logorrr.conf

import app.logorrr.OsxBridge
import app.logorrr.conf.SettingsIO.renderOptions
import app.logorrr.conf.mut.{MutLogFileSettings, MutSettings}
import app.logorrr.io.{FileId, FilePaths, Fs}
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

  private val mutSettings = new MutSettings

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
    mutSettings.setStageSettings(settings.stageSettings)
    mutSettings.setLogFileSettings(settings.fileSettings)
    mutSettings.setSomeActive(settings.someActive)
    mutSettings.setSomeLastUsedDirectory(settings.someLastUsedDirectory)

    setHostServices(hostServices)
  }

  /** a case class representing current setting state */
  def getSettings: Settings = mutSettings.petrify()

  def setSomeActiveLogFile(sActive: Option[FileId]): Unit = mutSettings.setSomeActive(sActive)

  def getSomeActiveLogFile: Option[FileId] = mutSettings.getSomeActiveLogFile

  def getSomeLastUsedDirectory: Option[Path] = mutSettings.getSomeLastUsedDirectory

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = mutSettings.setSomeLastUsedDirectory(someDirectory)

  def removeLogFile(fileId: FileId): Unit = timeR({
    mutSettings.removeLogFileSetting(fileId)
    mutSettings.setSomeActive(mutSettings.getSomeActiveLogFile match {
      case Some(value) if value == fileId => None
      case x => x
    })

    if (OsUtil.enableSecurityBookmarks) {
      if (fileId.isZipEntry) {
        // only release path if no other file is opened anymore for this particular zip file
        val zipInQuestion = fileId.extractZipFileId
        if (!LogoRRRGlobals.getOrderedLogFileSettings.map(_.fileId.extractZipFileId).contains(zipInQuestion)) {
          OsxBridge.releasePath(fileId.extractZipFileId.absolutePathAsString)
        }
      } else {
        OsxBridge.releasePath(fileId.absolutePathAsString)
      }
    }

  }, s"Removed file $fileId ...")

  def clearLogFileSettings(): Unit = mutSettings.clearLogFileSettings()

  def registerSettings(fs: LogFileSettings): Unit = mutSettings.putMutLogFileSetting(MutLogFileSettings(fs))

  def getLogFileSettings(fileId: FileId): MutLogFileSettings = mutSettings.getMutLogFileSetting(fileId)


}
