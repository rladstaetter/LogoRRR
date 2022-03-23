package app.logorrr.conf

import app.logorrr.model.LogFileSettings
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import scala.:+
import scala.collection.immutable.ListMap

object RecentFileSettings {

  implicit lazy val reader = deriveReader[RecentFileSettings]
  implicit lazy val writer = deriveWriter[RecentFileSettings]

}

/**
 * @param logFileDefinitions files which were last opened
 */
case class RecentFileSettings(logFileDefinitions: Map[String, LogFileSettings]
                              , someActiveLogReport: Option[String]) {

  val someActive: Option[LogFileSettings] = someActiveLogReport.flatMap(logFileDefinitions.get)

  def remove(pathAsString: String): RecentFileSettings = {
    val updatedActiveReport =
      someActiveLogReport match {
        case Some(value) if value == pathAsString => None
        case _ => None
      }
    copy(logFileDefinitions = logFileDefinitions - pathAsString, updatedActiveReport)
  }

  /** updates recent files with given log report definition */
  def update(definition: LogFileSettings): RecentFileSettings = {
    copy(logFileDefinitions + (definition.pathAsString -> definition))
  }

  def filterValids(): RecentFileSettings = copy(logFileDefinitions = logFileDefinitions.filter { case (_, d) => d.isPathValid })

  def clear(): RecentFileSettings = RecentFileSettings(Map(), None)

}
