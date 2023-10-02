package app.logorrr.util

/**
 * Determine which OS we are running on
 */
object OsUtil {


  sealed trait Os

  case object Windows extends Os

  case object Mac extends Os

  case object Linux extends Os

  val currentOs =
    if (System.getProperty("os.name").toLowerCase.contains("windows")) {
      Windows
    } else if (System.getProperty("os.name").toLowerCase.contains("mac")) {
      Mac
    } else {
      Linux
    }

  val isMac = currentOs == Mac
  val isWin = currentOs == Windows
  // val isLinux = currentOs == Linux

    // for releases / mac installers this value should always be true
    // set this flag only during development
  val enableSecurityBookmarks = false //isMac

  def osFun[T](onWin: => T, onMac: => T, onLinux: => T): T =
    if (isWin) {
      onWin
    } else if (isMac) {
      onMac
    } else onLinux
}
