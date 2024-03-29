package app.logorrr.meta

import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

object AppMeta {

  implicit lazy val reader: ConfigReader[AppMeta] = deriveReader[AppMeta]

  private def meta: AppMeta = ConfigSource.resources("meta.conf").load[AppMeta] match {
    case Right(value) => value
    case Left(_) => AppMeta("LogoRRR", "LATEST")
  }

  val fullAppName = s"${meta.appName}"
  val fullAppNameWithVersion = s"${meta.appName} ${meta.appVersion}"
  val appVersion: String = meta.appVersion

}

case class AppMeta(appName: String, appVersion: String)
