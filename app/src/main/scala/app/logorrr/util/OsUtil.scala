package app.logorrr.util

/**
 * Determine which OS we are running on
 */
object OsUtil {

  sealed trait Os

  case object Windows extends Os

  case object Mac extends Os

  val currentOs =
    if (System.getProperty("os.name").toLowerCase.contains("windows")) {
      Windows
    } else {
      Mac
    }


}
