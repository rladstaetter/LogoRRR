package app.logorrr.conf

import app.logorrr.LogoRRRApp
import app.logorrr.conf.mut.{MutLogFileSettings, MutSettings}
import app.logorrr.io.{OsxBridgeHelper, SettingsFileIO}
import app.logorrr.services.hostservices.LogoRRRHostServices
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path

/**
 * Place LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals extends TinyLog :

  private val mutSettings = new MutSettings

  private val hostServicesProperty = new SimpleObjectProperty[LogoRRRHostServices]()

  def searchTermGroupNames: ObservableList[String] = mutSettings.searchTermGroupNames

  def persist(settings: Settings): Unit = SettingsFileIO.toFile(settings, LogoRRRApp.paths.settingsFile)


  def getOrderedLogFileSettings: Seq[LogFileSettings] = mutSettings.getOrderedLogFileSettings

  def bindWindow(window: Window): Unit =
    window.setX(LogoRRRGlobals.getStageX)
    window.setY(LogoRRRGlobals.getStageY)
    window.setWidth(LogoRRRGlobals.getStageWidth)
    window.setHeight(LogoRRRGlobals.getStageHeight)

    mutSettings.bindWindowProperties(window)


  def putSearchTermGroup(stg: SearchTermGroup): Unit = mutSettings.putSearchTermGroup(stg)

  def removeSearchTermGroup(name: String): Unit = mutSettings.removeSearchTermGroup(name)

  def clearSearchTermGroups(): Unit = mutSettings.clearSearchTermGroups()

  def unbindWindow(): Unit = mutSettings.unbindWindow()

  def getStageWidth: Int = mutSettings.getStageWidth

  def getStageHeight: Int = mutSettings.getStageHeight

  def getStageX: Double = mutSettings.getStageX

  def getStageY: Double = mutSettings.getStageY

  def setHostServices(hostServices: LogoRRRHostServices): Unit = hostServicesProperty.set(hostServices)

  def getHostServices: LogoRRRHostServices = hostServicesProperty.get()

  def set(settings: Settings, hostServices: LogoRRRHostServices): Unit =
    mutSettings.setStageSettings(settings.stageSettings)
    mutSettings.setLogFileSettings(settings.fileSettings)
    mutSettings.setSomeActive(settings.someActive)
    mutSettings.setSomeLastUsedDirectory(settings.someLastUsedDirectory)

    // populate either from saved file or use default values.
    // if values are saved in the .conf file, those should be used
    val searchTermGroupsToUse = settings.searchTermGroups

    for (k, v) <- searchTermGroupsToUse do
      mutSettings.mutSearchTermGroupSettings.put(k, v)

    setHostServices(hostServices)

  def getSettings: Settings = mutSettings.mkImmutable()

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

    if OsUtil.enableSecurityBookmarks then {
      if fileId.isZipEntry then {
        // only release path if no other file is opened anymore for this particular zip file
        val zipInQuestion = fileId.extractZipFileId
        if !LogoRRRGlobals.getOrderedLogFileSettings.map(_.fileId.extractZipFileId).contains(zipInQuestion) then {
          val zipPath = fileId.extractZipFileId.asPath
          OsxBridgeHelper.releasePath(zipPath)
        }
      } else {
        val filePath = fileId.asPath
        OsxBridgeHelper.releasePath(filePath)
      }
    }

  }, s"Removed file $fileId ...")

  def clearLogFileSettings(): Unit = mutSettings.clearLogFileSettings()

  def registerSettings(fs: LogFileSettings): Unit = mutSettings.putMutLogFileSetting(MutLogFileSettings(fs))

  def getLogFileSettings(fileId: FileId): MutLogFileSettings = mutSettings.getMutLogFileSetting(fileId)

  val searchTermGroupEntries: ObservableList[SearchTermGroup] = mutSettings.mutSearchTermGroupSettings.searchTermGroupEntries


