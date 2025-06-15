package app.logorrr.io

import app.logorrr.util.OsUtil
import app.logorrr.util.OsUtil._

import java.nio.file.{Path, Paths}

/** paths which logorr writes / reads from */
object FilePaths {

  val settingsFileName = "logorrr.conf"

  val logFileName = "logorrr.log"

  /**
   * Attention Mac Users Start:
   *
   * System.getProperty("user.home") resolves to ~/Library/Containers/app.logorrr/ when launched via pkg
   * (sandboxed app). If run from IntelliJ, this resolves to /Users/<username> .
   *
   * That is, in sandboxed mode LogoRRR will create a conf file here:
   *
   * ~/Library/Containers/app.logorrr/Data/Library/Application Support/app.logorrr/logorrr.conf
   *
   * See also:
   *
   * https://stackoverflow.com/questions/9495503/applications-data-folder-in-mac
   * https://apple.stackexchange.com/questions/28928/what-is-the-macos-equivalent-to-windows-appdata-folder/28930#28930
   * https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/FileSystemProgrammingGuide/MacOSXDirectories/MacOSXDirectories.html
   *
   * Attention Mac Users End:
   */
  val confPathMap: Map[Os, Path] =
    Map(Windows -> Paths.get("C:/ProgramData/LogoRRR/")
      , Mac -> Paths.get(System.getProperty("user.home")).resolve("Library/Application Support/app.logorrr/")
      , Linux -> Paths.get(System.getProperty("user.home")).resolve(".logorrr/")
      , LinuxFlatPak -> Option(System.getProperty("XDG_CONFIG_HOME")).map(p => Paths.get(p)).orNull
    )

  val logPathMap: Map[Os, Path] =
    confPathMap ++ Map(LinuxFlatPak -> Option(System.getProperty("XDG_DATA_HOME")).map(p => Paths.get(p)).orNull)

  val settingsFilePath: Path = confPathMap(OsUtil.currentOs).resolve(settingsFileName)

  val logFilePath: Path = logPathMap(OsUtil.currentOs).resolve(logFileName)

}
