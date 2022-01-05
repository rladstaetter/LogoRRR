package app.logorrr.conf

import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.util.{CanLog, JfxUtils}
import com.typesafe.config.ConfigRenderOptions
import javafx.scene.image.Image
import javafx.stage.Window
import pureconfig.generic.auto._
import pureconfig.{ConfigSource, ConfigWriter}

import scala.util.{Failure, Success, Try}

object Settings extends CanLog {

  /** turn of ugly 'hardcoded value' messages */
  lazy val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  lazy val default: Settings = ConfigSource.default.loadOrThrow[Settings]

  def read(): Settings = ConfigSource.file(FilePaths.settingsFilePath).loadOrThrow[Settings]

  /** persists settings */
  def write(settings: Settings): Unit = {
    Fs.write(FilePaths.settingsFilePath, ConfigWriter[Settings].to(settings).render(renderOptions))
  }

  lazy val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

  lazy val meta = ConfigSource.resources("meta.conf").load[AppMeta] match {
    case Right(value) => value
    case Left(e) =>
      logError(e.prettyPrint())
      AppMeta("LogoRRR", "LATEST")
  }

  lazy val fullAppName = Settings.meta.appName + " " + Settings.meta.appVersion

  lazy val Default = Settings(StageSettings(0, 0, 500, 500)
    , SquareImageSettings(10)
    , RecentFileSettings(Seq()))

  lazy val someSettings: Option[Settings] =
    Try(read()) match {
      case Success(settings) =>
        logInfo(s"Loaded settings from ${FilePaths.settingsFilePath.toAbsolutePath} ...")
        Option(settings)
      case Failure(_) =>
        Try(default) match {
          case Success(defaultSettings) =>
            logError(s"Could not load settings, reinitializing ${FilePaths.settingsFilePath.toAbsolutePath} with default settings ...")
            Settings.write(defaultSettings)
            Option(defaultSettings)
          // should not happen, programming / deployment error
          case Failure(e) =>
            logException(e)
            logError("Using following fallback configuration:")
            logError("")
            logError(ConfigWriter[Settings].to(Default).render(renderOptions))
            logError("")
            Option(Default)
        }
    }

}

/**
 * @param files files which were last opened
 */
case class RecentFileSettings(files: Seq[String])

object StageSettings {

  val stageWidthListener = JfxUtils.onNew[Number](n => {
    val settings1 = Settings.read()
    val newStageSettings = settings1.stageSettings.copy(width = n.intValue())
    Settings.write(settings1.copy(stageSettings = newStageSettings))
  })

  val stageHeightListener = JfxUtils.onNew[Number](n => {
    val settings1 = Settings.read()
    val newStageSettings = settings1.stageSettings.copy(height = n.intValue())
    Settings.write(settings1.copy(stageSettings = newStageSettings))
  })

  val stageXListener = JfxUtils.onNew[Number](xValue => {
    val settings1 = Settings.read()
    val newStageSettings = settings1.stageSettings.copy(x = xValue.doubleValue())
    Settings.write(settings1.copy(stageSettings = newStageSettings))
  })

  val stageYListener = JfxUtils.onNew[Number](n => {
    val settings1 = Settings.read()
    val newStageSettings = settings1.stageSettings.copy(y = n.doubleValue())
    Settings.write(settings1.copy(stageSettings = newStageSettings))
  })

  def addWindowListeners(window: Window): Unit = {
    window.xProperty().addListener(StageSettings.stageXListener)
    window.yProperty().addListener(StageSettings.stageYListener)
    window.widthProperty().addListener(StageSettings.stageWidthListener)
    window.heightProperty().addListener(StageSettings.stageHeightListener)
  }

  def removeWindowListeners(window: Window): Unit = {
    window.xProperty().removeListener(StageSettings.stageXListener)
    window.yProperty().removeListener(StageSettings.stageYListener)
    window.widthProperty().removeListener(StageSettings.stageWidthListener)
    window.heightProperty().removeListener(StageSettings.stageHeightListener)
  }

}

/**
 * @param x upper left x cooordinate for Logorrr
 * @param y upper left y cooordinate for Logorrr
 * @param width width of stage
 * @param height height of stage
 */
case class StageSettings(x: Double
                         , y: Double
                         , width: Int
                         , height: Int)

/**
 * @param width width of rectangles
 */
case class SquareImageSettings(width: Int)

case class Settings(stageSettings: StageSettings
                    , squareImageSettings: SquareImageSettings
                    , recentFiles: RecentFileSettings)


