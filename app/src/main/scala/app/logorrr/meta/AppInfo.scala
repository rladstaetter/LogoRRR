package app.logorrr.meta

import pureconfig.generic.semiauto.deriveReader
import pureconfig.{ConfigReader, ConfigSource}

object AppInfo {

  implicit lazy val reader: ConfigReader[AppInfo] = deriveReader[AppInfo]

  private def meta: AppInfo = ConfigSource.resources("meta.conf").load[AppInfo] match {
    case Right(value) => value
    case Left(_) => AppInfo("LogoRRR", "LATEST")
  }

  val fullAppName = s"${meta.appName}"
  val fullAppNameWithVersion = s"${meta.appName} ${meta.appVersion}"
  val appVersion: String = meta.appVersion

}

case class AppInfo(appName: String, appVersion: String)
