package app.logorrr.conf

import app.logorrr.LogoRRRApp
import app.logorrr.conf.mut.{MutLogFileSettings, MutSearchTermGroup, MutSettings, MutTimestampSettings}
import app.logorrr.io.{OsxBridgeHelper, SettingsFileIO}
import app.logorrr.services.hostservices.LogoRRRHostServices
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.stage.Window
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

import java.nio.file.Path
import scala.jdk.CollectionConverters.*


/**
 * LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals extends TinyLog:

  val mutSettings = new MutSettings

  private val hostServicesProperty = new SimpleObjectProperty[LogoRRRHostServices]()

  /** * Getter for the default group.
   *    It searches through the mutable entries to find the one with selected == true.
   */
  def getDefaultSearchTermGroup: Option[MutSearchTermGroup] =
    import scala.jdk.CollectionConverters.*
    searchTermGroupEntries.asScala.find(_.isSelected)

  /** * Sets the default group by updating the mutable properties of the items.
   */
  def setDefaultSearchTermGroup(stg: MutSearchTermGroup): Unit =
    mutSettings.mutSearchTermGroupSettings.setSelected(stg)

  def persist(settings: Settings): Unit = SettingsFileIO.toFile(settings, LogoRRRApp.paths.settingsFile)

  def getOrderedLogFileSettings: Seq[LogFileSettings] = mutSettings.getOrderedLogFileSettings

  def bindWindow(window: Window): Unit =
    window.setX(LogoRRRGlobals.getStageX)
    window.setY(LogoRRRGlobals.getStageY)
    window.setWidth(LogoRRRGlobals.getStageWidth)
    window.setHeight(LogoRRRGlobals.getStageHeight)
    mutSettings.bindWindowProperties(window)

  def add(stg: MutSearchTermGroup): Unit = mutSettings.mutSearchTermGroupSettings.add(stg)

  def remove(stg: MutSearchTermGroup): Unit = mutSettings.mutSearchTermGroupSettings.remove(stg)

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
    mutSettings.setSomeLastUsedDirectory(settings.someLastUsedDirectory)
    settings.someTimestampSettings match {
      case Some(timestampSettings) => setTimestampSettings(MutTimestampSettings(timestampSettings))
      case None => setTimestampSettings(null)
    }

    // Populate from immutable settings into the mutable properties
    mutSettings.mutSearchTermGroupSettings.searchTermGroupEntries.setAll(
      settings.searchTermGroups.map(MutSearchTermGroup.apply).asJava
    )

    setHostServices(hostServices)

  def getSettings: Settings = mutSettings.mkImmutable()

  def getSomeActiveLogFile: Option[FileId] = mutSettings.getSomeActiveLogFile

  def getSomeLastUsedDirectory: Option[Path] = mutSettings.getSomeLastUsedDirectory

  def setSomeLastUsedDirectory(someDirectory: Option[Path]): Unit = mutSettings.setSomeLastUsedDirectory(someDirectory)

  def removeLogFile(fileId: FileId): Unit = {
    mutSettings.removeLogFileSetting(fileId)
    if OsUtil.enableSecurityBookmarks then {
      if fileId.isZipEntry then {
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
  }

  def clearLogFileSettings(): Unit = mutSettings.clearLogFileSettings()

  def registerSettings(fs: LogFileSettings): Unit = mutSettings.putMutLogFileSetting(MutLogFileSettings(fs))

  def getLogFileSettings(fileId: FileId): MutLogFileSettings = mutSettings.getMutLogFileSetting(fileId)

  val searchTermGroupEntries: ObservableList[MutSearchTermGroup] = mutSettings.mutSearchTermGroupSettings.searchTermGroupEntries

  def getTimestampSettings: Option[MutTimestampSettings] = Option(mutSettings.getTimestampSettings)

  def setTimestampSettings(timestampSettings: MutTimestampSettings): Unit = mutSettings.setTimestampSettings(timestampSettings)
