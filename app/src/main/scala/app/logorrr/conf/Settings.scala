package app.logorrr.conf

import app.logorrr.io.{FilePaths, Fs}
import app.logorrr.util.CanLog
import com.typesafe.config.ConfigRenderOptions
import javafx.scene.image.Image
import pureconfig.generic.auto._
import pureconfig.{ConfigSource, ConfigWriter}

import java.nio.file.Path

object Settings extends CanLog {

  /** turn of ugly 'hardcoded value' messages */
  lazy val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  def write(path: Path, settings: Settings): Unit = {
    Fs.write(path, ConfigWriter[Settings].to(settings).render(renderOptions))
  }

  lazy val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))

  lazy val meta = ConfigSource.resources("meta.conf").load[AppMeta] match {
    case Right(value) => value
    case Left(e) =>
      logError(e.prettyPrint())
      AppMeta("LogoRRR", "LATEST")
  }

  lazy val Default = Settings(StageSettings(0, 0, 500, 500)
    , SquareImageSettings(10)
    , RecentFileSettings(Seq()))

  lazy val someSettings: Option[Settings] =
    ConfigSource.file(FilePaths.settingsFilePath).load[Settings] match {
      case Right(settings) =>
        logInfo(s"Loaded settings from ${FilePaths.settingsFilePath.toAbsolutePath} ...")
        Option(settings)
      case Left(_) =>
        ConfigSource.default.load[Settings] match {
          case Right(defaultSettings) =>
            logError(s"Could not load settings, reinitializing ${FilePaths.settingsFilePath.toAbsolutePath} with default settings ...")
            Settings.write(FilePaths.settingsFilePath, defaultSettings)
            Option(defaultSettings)
          // should not happen, programming / deployment error
          case Left(e) =>
            logError(e.prettyPrint())
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

/**
 * @param x upper left x cooordinate for Logorrr
 * @param y upper left y cooordinate for Logorrr
 * @param width width of stage
 * @param height height of stage
 */
case class StageSettings(x: Int
                         , y: Int
                         , width: Int
                         , height: Int)

/**
 * @param width width of rectangles
 */
case class SquareImageSettings(width: Int)

case class Settings(stageSettings: StageSettings
                    , squareImageSettings: SquareImageSettings
                    , recentFiles: RecentFileSettings)


