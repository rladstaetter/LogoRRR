package app.logorrr.conf

import app.logorrr.BuildProps

object AppInfo:

  def apply(appName: String, buildProps: BuildProps): AppInfo =
    AppInfo(appName, buildProps.revision, buildProps.version, buildProps.formattedTimestamp)


case class AppInfo(appName: String
                   , revision: String
                   , version: String
                   , timestamp: String):
  val nameAndVersion = s"$appName $version"
  val asString = s"$appName $version $revision $timestamp"
