package app.logorrr.conf

import app.logorrr.io.IoManager
import app.logorrr.views.search.stg.SearchTermGroup
import javafx.geometry.Rectangle2D
import javafx.scene.paint.Color
import javafx.stage.Screen
import upickle.default.*

import java.nio.file.{Path, Paths}

/**
 * Global settings for LogoRRR
 *
 * LogoRRR tries to remember as much as possible from last run, in order to give user a headstart from where they last
 * left. The idea is that the user doesn't need to fiddle around with settings every time.
 */
object Settings {

  // 1. Define how to read/write a single Path
  implicit val pathRW: ReadWriter[Path] = readwriter[String].bimap[Path](
    path => path.toString, // How to write: Path -> String
    str => Paths.get(str) // How to read:  String -> Path
  )

  private val EmptyGroup: SearchTermGroup = SearchTermGroup("empty", Seq())

  val JavaLoggingGroup: SearchTermGroup = SearchTermGroup("default", Seq(
    SearchTerm("FINEST", Color.GREY, active = true)
    , SearchTerm("INFO", Color.GREEN, active = true)
    , SearchTerm("WARNING", Color.ORANGE, active = true)
    , SearchTerm("SEVERE", Color.RED, active = true)
  ))

  val DefaultSearchTermGroups: Seq[SearchTermGroup] = Seq(EmptyGroup, JavaLoggingGroup)

  def calcDefaultScreenPosition(): Rectangle2D = {

    val ps: Rectangle2D = Screen.getPrimary.getVisualBounds

    val originalX = ps.getMinX
    val originalY = ps.getMinY
    val originalWidth = ps.getWidth
    val originalHeight = ps.getHeight

    // Calculate the dimensions of the new rectangle (80% of original)
    val newWidth = originalWidth * 0.8
    val newHeight = originalHeight * 0.8

    // Calculate the new coordinates
    val newX = originalX + (originalWidth - newWidth) / 2
    val newY = originalY + (originalHeight - newHeight) / 2
    new Rectangle2D(newX, newY, newWidth, newHeight)
  }

  lazy val Default: Settings = Settings(
    StageSettings(calcDefaultScreenPosition())
    , Map()
    , None
    , None
    , DefaultSearchTermGroups.map(stg => stg.name -> stg.terms).toMap
  )

}

case class Settings(stageSettings: StageSettings
                    , fileSettings: Map[String, LogFileSettings]
                    , someActive: Option[FileId]
                    , someLastUsedDirectory: Option[Path] = None
                    , searchTermGroups: Map[String, Seq[SearchTerm]]) derives ReadWriter {

  /** updates recent files with given log setting */
  def update(logFileSetting: LogFileSettings): Settings = {
    copy(stageSettings, fileSettings + (logFileSetting.fileId.value -> logFileSetting))
  }

  def filterWithValidPaths(): Settings = copy(fileSettings = fileSettings.filter { case (_, d) =>
    // if entry is part of a zip file, test the path of the zip file
    if (d.fileId.isZipEntry) {
      IoManager.isPathValid(d.fileId.extractZipFileId.asPath)
    } else {
      IoManager.isPathValid(d.path)
    }
  })


}