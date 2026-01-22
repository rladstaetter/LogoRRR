package app.logorrr.conf

import app.logorrr.conf
import app.logorrr.io.IoManager
import app.logorrr.util.JfxUtils
import net.ladstatt.util.log.TinyLog
import upickle.default.*

import java.nio.file.{Path, Paths}


/**
 * Global settings for LogoRRR
 *
 * LogoRRR tries to remember as much as possible from last run, in order to give user a headstart from where they last
 * left. The idea is that the user doesn't need to fiddle around with settings every time.
 */
object Settings extends TinyLog:

  // 1. Define how to read/write a single Path
  implicit val pathRW: ReadWriter[Path] = readwriter[String].bimap[Path](
    path => path.toString, // How to write: Path -> String
    str => Paths.get(str) // How to read:  String -> Path
  )

  val DefaultSearchTermGroups: Seq[SearchTermGroup] = conf.DefaultSearchTermGroups().searchTermGroups

  lazy val Default: Settings = Settings(
    StageSettings(JfxUtils.calcDefaultScreenPosition())
    , Map()
    , None
    , None
    , DefaultSearchTermGroups.map(stg => stg.name -> stg.terms).toMap
    , None
  )


/**
 * Settings for Logorrr which are persisted in the applications configuration folder (logorrr.json)
 *
 * @param stageSettings         settings regarding stage (width, height ...)
 * @param fileSettings          contains settings specific for a given log file
 * @param someActive            which logfile is active (or none)
 * @param someLastUsedDirectory which directory was last accessed (for open file ...)
 * @param searchTermGroups      global search term groups
 * @param someTimestampSettings global defaults for timestamp settings
 */
case class Settings(stageSettings: StageSettings
                    , fileSettings: Map[String, LogFileSettings]
                    , someActive: Option[FileId]
                    , someLastUsedDirectory: Option[Path] = None
                    , searchTermGroups: Map[String, Seq[SearchTerm]]
                    , someTimestampSettings: Option[TimestampSettings] = None) derives ReadWriter {

  /** updates recent files with given log setting */
  def update(logFileSetting: LogFileSettings): Settings =
    copy(stageSettings, fileSettings + (logFileSetting.fileId.value -> logFileSetting))

  def filterWithValidPaths(): Settings = copy(fileSettings = fileSettings.filter { case (_, d) =>
    // if entry is part of a zip file, test the path of the zip file
    if d.fileId.isZipEntry then {
      IoManager.isPathValid(d.fileId.extractZipFileId.asPath)
    } else {
      IoManager.isPathValid(d.path)
    }
  })


}