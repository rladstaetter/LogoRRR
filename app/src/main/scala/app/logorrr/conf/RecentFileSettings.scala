package app.logorrr.conf

import app.logorrr.model.LogReportDefinition
import pureconfig.generic.semiauto.{deriveReader, deriveWriter}

object RecentFileSettings {

  implicit lazy val reader = deriveReader[RecentFileSettings]
  implicit lazy val writer = deriveWriter[RecentFileSettings]

}

/**
 * @param logReportDefinition files which were last opened
 */
case class RecentFileSettings(logReportDefinition: Seq[LogReportDefinition]) {
  /** updates recent files with given log report definition */
  def update(definition: LogReportDefinition): RecentFileSettings = {
    RecentFileSettings(for (ld <- logReportDefinition) yield {
      if (ld.pathAsString == definition.pathAsString) {
        definition
      } else ld
    })
  }


  def clear(): RecentFileSettings = RecentFileSettings(Seq())

  def filterValids(): RecentFileSettings = copy(logReportDefinition = logReportDefinition.filter(_.isPathValid))

}
