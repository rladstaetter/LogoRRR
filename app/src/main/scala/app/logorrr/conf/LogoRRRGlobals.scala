package app.logorrr.conf

import app.logorrr.LogoRRRApp
import app.logorrr.conf.mut.{MutLogFileSettings, MutSearchTermGroup, MutSettings, MutTimeSettings}
import app.logorrr.io.{OsxBridgeHelper, SettingsFileIO}
import app.logorrr.services.hostservices.LogoRRRHostServices
import app.logorrr.util.PersistenceManager
import javafx.beans.property.{Property, SimpleListProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path


/**
 * LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals extends TinyLog:

  val persistenceManager = new PersistenceManager()

  val mutSettings = new MutSettings

  private val hostServicesProperty = new SimpleObjectProperty[LogoRRRHostServices]()

  /** * Sets the default group by updating the mutable properties of the items.
   */
  def setDefaultSearchTermGroup(stg: MutSearchTermGroup): Unit =
    mutSettings.mutSearchTermGroupSettings.setSelected(stg)

  def persist(settings: Settings): Unit = SettingsFileIO.toFile(settings, LogoRRRApp.paths.settingsFile)

  def fileIds: Set[FileId] = mutSettings.fileIds.get()

  def getMutLogFileSettings: ObservableList[MutLogFileSettings] = mutSettings.getMutLogFileSettings

  def bindWindow(window: Window): Unit =
    window.setX(LogoRRRGlobals.getStageX)
    window.setY(LogoRRRGlobals.getStageY)
    window.setWidth(LogoRRRGlobals.getStageWidth)
    window.setHeight(LogoRRRGlobals.getStageHeight)
    mutSettings.bindWindowProperties(window)

  def add(stg: MutSearchTermGroup): Unit = mutSettings.mutSearchTermGroupSettings.add(stg)

  def remove(stg: MutSearchTermGroup): Unit = mutSettings.mutSearchTermGroupSettings.remove(stg)

  def clearSearchTermGroups(): Unit = mutSettings.clearSearchTermGroups()

  def shutdown(): Unit =
    mutSettings.unbindWindow()
    persistenceManager.shutdown()

  def getStageWidth: Int = mutSettings.getStageWidth

  def getStageHeight: Int = mutSettings.getStageHeight

  def getStageX: Double = mutSettings.getStageX

  def getStageY: Double = mutSettings.getStageY

  def setHostServices(hostServices: LogoRRRHostServices): Unit = hostServicesProperty.set(hostServices)

  def getHostServices: LogoRRRHostServices = hostServicesProperty.get()

  def set(settings: Settings, hostServices: LogoRRRHostServices): Unit =
    mutSettings.set(persistenceManager, settings)
    settings.someTimeSettings match {
      case Some(timestampSettings) => setTimeSettings(MutTimeSettings(timestampSettings))
      case None => setTimeSettings(MutTimeSettings(TimeSettings.Invalid))
    }
    setHostServices(hostServices)

  def getSettings: Settings = mutSettings.mkImmutable()

  def getSomeActiveLogFile: Option[FileId] = mutSettings.getSomeActiveLogFile

  def getSomeLastUsedDirectory: Option[Path] = mutSettings.getSomeLastUsedDirectory

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = mutSettings.setSomeLastUsedDirectory(someDirectory)

  def removeLogFile(fileId: FileId): Unit = {
    mutSettings.removeLogFile(fileId)
    if OsUtil.enableSecurityBookmarks then {
      if fileId.isZipEntry then {
        val zipInQuestion = fileId.extractZipFileId
        // if !LogoRRRGlobals.getOrderedLogFileSettings.map(_.fileId.extractZipFileId).contains(zipInQuestion) then {
        // only if this is the last zip entry release the zip
        if !LogoRRRGlobals.fileIds.map(f => f.extractZipFileId).exists(_.equals(zipInQuestion)) then {
          val zipPath = fileId.extractZipFileId.asPath
          OsxBridgeHelper.releasePath(zipPath)
        }
      } else {
        val filePath = fileId.asPath
        OsxBridgeHelper.releasePath(filePath)
      }
    }
  }

  def clearLogFileSettings(): Unit = mutSettings.clearLogFileSettings()

  def registerSettings(fs: LogFileSettings): MutLogFileSettings = {
    val mutSettings = MutLogFileSettings(fs)
    registerSettings(mutSettings)
    mutSettings
  }

  def registerSettings(settings: MutLogFileSettings): Unit =
    persistenceManager.init(settings.getFileId, settings.allProps)
    mutSettings.add(settings)


  def getLogFileSettings(fileId: FileId): MutLogFileSettings = mutSettings.getMutLogFileSetting(fileId)

  val searchTermGroupEntries: SimpleListProperty[MutSearchTermGroup] = mutSettings.mutSearchTermGroupSettings.searchTermGroupEntries

  def timeSettings: MutTimeSettings = mutSettings.timeSettings

  def setTimeSettings(timesettings: MutTimeSettings): Unit = mutSettings.setTimeSettings(timesettings)

  val allProps: Set[Property[?]] =
    mutSettings.allProps