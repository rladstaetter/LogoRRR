package app.logorrr.conf

import app.logorrr.conf.SettingsIO.renderOptions
import app.logorrr.conf.mut.{MutLogFileSettings, MutSettings, MutStageSettings}
import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.model.LogFileSettings
import app.logorrr.util.JfxUtils
import javafx.application.HostServices
import javafx.beans.property.{SimpleListProperty, SimpleMapProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.stage.Window
import pureconfig.ConfigWriter

import scala.jdk.CollectionConverters._

/**
 * Place LogoRRR's settings.
 *
 * The user can change certain values via interacting or explicitly setting values in the preferences dialog.
 */
object LogoRRRGlobals {
  val sceneProperty = new SimpleObjectProperty[Scene]()

  def setScene(scene: Scene): Unit = sceneProperty.set(scene)

  def getScene(): Scene = sceneProperty.get()

  private val stageWidthListener =
    JfxUtils.onNew[Number]((n: Number) => LogoRRRGlobals.updateStageSettings(_.copy(width = n.intValue())))

  private val stageHeightListener =
    JfxUtils.onNew[Number]((n: Number) => LogoRRRGlobals.updateStageSettings(_.copy(height = n.intValue())))

  private val stageXListener =
    JfxUtils.onNew2[Number]((oldX: Number, newX: Number) => {
      Option(oldX) match {
        case Some(value) if (value != newX) && !java.lang.Double.isNaN(value.doubleValue()) =>
          LogoRRRGlobals.updateStageSettings(_.copy(x = newX.doubleValue()))
        case _ =>
      }
    })

  val stageYListener =
    JfxUtils.onNew2[Number]((oldY: Number, newY: Number) => {
      Option(oldY) match {
        case Some(value) if (value != newY) && !java.lang.Double.isNaN(value.doubleValue()) =>
          LogoRRRGlobals.updateStageSettings(_.copy(y = newY.doubleValue()))
        case _ =>
      }
    })

  def bindWindow(window: Window): Unit = {
    settings.stageSettings.widthProperty.bind(window.getScene.widthProperty())
    settings.stageSettings.widthProperty.addListener(stageWidthListener)

    settings.stageSettings.heightProperty.bind(window.getScene.heightProperty())
    settings.stageSettings.heightProperty.addListener(stageHeightListener)

    settings.stageSettings.xProperty.bind(window.xProperty())
    settings.stageSettings.xProperty.addListener(stageXListener)

    settings.stageSettings.yProperty.bind(window.yProperty())
    settings.stageSettings.yProperty.addListener(stageYListener)
  }

  def unbindWindow(): Unit = {
    settings.stageSettings.widthProperty.unbind()
    settings.stageSettings.widthProperty.removeListener(stageWidthListener)

    settings.stageSettings.heightProperty.unbind()
    settings.stageSettings.heightProperty.removeListener(stageHeightListener)

    settings.stageSettings.xProperty.unbind()
    settings.stageSettings.xProperty.removeListener(stageXListener)

    settings.stageSettings.yProperty.unbind()
    settings.stageSettings.yProperty.removeListener(stageYListener)

  }

  def getStageWidth(): Int = settings.stageSettings.widthProperty.get()

  def getStageHeight(): Int = settings.stageSettings.heightProperty.get()

  def getStageX(): Double = settings.stageSettings.xProperty.get()

  def getStageY(): Double = settings.stageSettings.yProperty.get()

  private val settings = new MutSettings
  private val hostServicesProperty = new SimpleObjectProperty[HostServices]()

  def setHostServices(hostServices: HostServices): Unit = hostServicesProperty.set(hostServices)

  def getHostServices: HostServices = hostServicesProperty.get()

  def set(settings: Settings, hostServices: HostServices): Unit = {
    this.settings.set(settings)
    setHostServices(hostServices)
  }

  def get(): Settings = settings.petrify()

  /** persists settings */
  def write(settings: Settings): Unit = {
    Fs.write(FilePaths.settingsFilePath, ConfigWriter[Settings].to(settings).render(renderOptions))
  }

  private def update(fn: Settings => Settings): Unit = write(fn(SettingsIO.fromFile()))

  def resetLogs(): Unit = update {
    s => s.copy(logFileSettings = Map(), logFileOrdering = Seq(), someActive = None)
  }

  def updateActive(sActive: Option[String]): Unit = update(s => s.copy(someActive = sActive))

  def updateLogFile(path: String, fs: LogFileSettings): Unit = update(s => s.copy(logFileSettings = s.logFileSettings + (path -> fs)))

  def removeLogFile(path: String): Unit = update(s => {
    s.copy(logFileSettings = s.logFileSettings - path
      , logFileOrdering = s.logFileOrdering.filterNot(_ == path)
      , someActive = s.someActive match {
        case Some(value) if value == path => None
        case x => x
      })
  })

  def updateBlockSettings(pathAsString: String, bs: BlockSettings): Unit = update {
    s =>
      val ls = s.logFileSettings(pathAsString).copy(blockSettings = bs)
      s.copy(logFileSettings = s.logFileSettings + (pathAsString -> ls))
  }

  def updateDividerPosition(pathAsString: String, dividerPosition: Double): Unit = update {
    s =>
      val lfs = s.logFileSettings(pathAsString).copy(dividerPosition = dividerPosition)
      s.copy(logFileSettings = s.logFileSettings + (pathAsString -> lfs))
  }

  def updateStageSettings(updateFn: StageSettings => StageSettings): Unit =
    update(s => s.copy(stageSettings = updateFn(s.stageSettings)))
}
