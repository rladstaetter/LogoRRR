package app.logorrr

import app.logorrr.cp.PropsCp

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.util.Properties

object BuildProps:

  lazy val Instance = BuildProps(PropsCp("/build.properties").asProperties(getClass))

  def apply(properties: Properties): BuildProps = {
    val name: String = Option(properties.getProperty("name")).getOrElse("app.logorrr.app")
    val revision: String = Option(properties.getProperty("revision")).getOrElse("LATEST")
    val timestamp: Long = Option(properties.getProperty("timestamp")).map(_.toLong).getOrElse(System.currentTimeMillis())
    val version: String = Option(properties.getProperty("version")).getOrElse("LATEST")
    new BuildProps(name, revision, timestamp, version)
  }

/**
 * build.properties is created via maven plugin 'buildnumber-maven-plugin' during build.
 */
case class BuildProps(name: String
                      , revision: String
                      , timestamp: Long
                      , version: String) {

  val formattedTimestamp: String =
    val PATTERN_FORMAT = "dd.MM.yyyy"
    val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault())
    formatter.format(Instant.ofEpochMilli(timestamp))

}