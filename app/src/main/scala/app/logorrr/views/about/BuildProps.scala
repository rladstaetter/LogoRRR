package app.logorrr.views.about

import app.logorrr.cp.PropsCp
import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Properties

object BuildProps:

  lazy val Instance = new BuildProps


class BuildProps {

  lazy val buildProps: Properties = PropsCp("/build.properties").asProperties(getClass)

  lazy val githash: String = buildProps.getProperty("revision")

  lazy val timestamp: String =
    val PATTERN_FORMAT = "dd.MM.yyyy"
    val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
      .withZone(ZoneId.systemDefault())
    val i = Instant.ofEpochMilli(buildProps.getProperty("timestamp").toLong)
    formatter.format(i)

}