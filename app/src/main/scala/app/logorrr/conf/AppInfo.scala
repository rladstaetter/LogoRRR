package app.logorrr.conf

import app.logorrr.cp.TxtCp
import upickle.default._

import scala.util.{Failure, Success}

object AppInfo {

  implicit lazy val rw: ReadWriter[AppInfo] = macroRW

  val meta = TxtCp("meta.json").asString() match {
    case Success(value) => read[AppInfo](value)
    case Failure(exception) => AppInfo("LogoRRR", "LATEST")
  }

  val fullAppName = s"${meta.appName}"
  val fullAppNameWithVersion = s"${meta.appName} ${meta.appVersion}"
  val appVersion: String = meta.appVersion

}

case class AppInfo(appName: String, appVersion: String)