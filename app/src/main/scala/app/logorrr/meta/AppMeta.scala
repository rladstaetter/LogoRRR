package app.logorrr.meta

import pureconfig.ConfigSource
import pureconfig.generic.semiauto.deriveReader

object AppMeta {

  implicit lazy val reader = deriveReader[AppMeta]

  private def meta: AppMeta = ConfigSource.resources("meta.conf").load[AppMeta] match {
    case Right(value) => value
    case Left(e) => AppMeta("LogoRRR", "LATEST")
  }

  val fullAppName = meta.appName + " " + meta.appVersion
  val appVersion = meta.appVersion

}

case class AppMeta(appName: String, appVersion: String)
