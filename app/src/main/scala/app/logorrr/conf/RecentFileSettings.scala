package app.logorrr.conf

import app.logorrr.model.LogReportDefinition
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

import scala.:+
import scala.collection.immutable.ListMap

object RecentFileSettings {

  implicit lazy val reader = deriveReader[RecentFileSettings]
  implicit lazy val writer = deriveWriter[RecentFileSettings]

}

/**
 * @param logReportDefinitions files which were last opened
 */
case class RecentFileSettings(logReportDefinitions: Map[String, LogReportDefinition]
                              , someActiveLogReport: Option[String]) {

  // remove in favor of someActiveLogReport
  val someActive: Option[LogReportDefinition] = logReportDefinitions.values.find(_.active)

  def remove(pathAsString: String): RecentFileSettings = {
    val updatedActiveReport =
      someActiveLogReport match {
        case Some(value) if value == pathAsString => None
        case None => None
      }
    copy(logReportDefinitions = logReportDefinitions - pathAsString, updatedActiveReport)
  }

  /** updates recent files with given log report definition */
  def update(definition: LogReportDefinition): RecentFileSettings = {
    copy(logReportDefinitions + (definition.pathAsString -> definition))
  }

  def filterValids(): RecentFileSettings = copy(logReportDefinitions = logReportDefinitions.filter { case (_, d) => d.isPathValid })

  def clear(): RecentFileSettings = RecentFileSettings(Map(), None)

}
