package app.logorrr.io

import net.ladstatt.util.os.OsUtil
import net.ladstatt.util.os.OsUtil.{Linux, LinuxFlatPak, LinuxSnap, Mac, Os, Windows}

import java.nio.file.{Path, Paths}

case class AppPaths(appId: String, groupId: String):

  lazy val pathMap: Map[Os, Path] =
    Map(Windows -> Paths.get(s"C:/ProgramData/$groupId/")
      , Mac -> Paths.get(System.getProperty("user.home")).resolve(s"Library/Application Support/$groupId/")
      , Linux -> Paths.get(System.getProperty("user.home")).resolve(s".$groupId/")
      , LinuxSnap -> Option(System.getenv("SNAP_USER_DATA")).map(p => Paths.get(p)).orNull
      , LinuxFlatPak -> Option(System.getenv("XDG_CONFIG_HOME")).map(p => Paths.get(p)).orNull
    )

  /** returns path where application stores its transient data (like configuration or logs) */
  def appDataDirectory: Path = pathMap(OsUtil.currentOs)

  /** path of the application log file */
  def logFile: Path = pathMap(OsUtil.currentOs).resolve(appId + ".log")

  /** path of the application's configuration file */
  def settingsFile: Path = pathMap(OsUtil.currentOs).resolve(appId + ".json")

