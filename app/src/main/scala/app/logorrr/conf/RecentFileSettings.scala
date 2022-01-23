package app.logorrr.conf

import app.logorrr.model.LogReportDefinition
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object RecentFileSettings {

  implicit lazy val reader = deriveReader[RecentFileSettings]
  implicit lazy val writer = deriveWriter[RecentFileSettings]

}

/**
 * @param logReportDefinitions files which were last opened
 */
case class RecentFileSettings(logReportDefinitions: Seq[LogReportDefinition]) {

  val someActive: Option[LogReportDefinition] = logReportDefinitions.find(_.active)

  /** updates recent files with given log report definition */
  def update(definition: LogReportDefinition): RecentFileSettings = {
    RecentFileSettings(for (ld <- logReportDefinitions) yield {
      if (ld.pathAsString == definition.pathAsString) {
        definition
      } else ld
    })
  }


  def clear(): RecentFileSettings = RecentFileSettings(Seq())

  def filterValids(): RecentFileSettings = copy(logReportDefinitions = logReportDefinitions.filter(_.isPathValid))

}
