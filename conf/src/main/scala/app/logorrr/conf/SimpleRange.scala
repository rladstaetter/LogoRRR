package app.logorrr.conf

import com.typesafe.config.ConfigValueFactory
import pureconfig.*
import pureconfig.generic.derivation.default.*

import scala.jdk.CollectionConverters.*


object SimpleRange {

  given reader: ConfigReader[SimpleRange] = ConfigReader.derived[SimpleRange]

  given writer: ConfigWriter[SimpleRange] = ConfigWriter.fromFunction(r => {
    ConfigValueFactory.fromMap(
      Map(
        "start" -> r.start
        , "end" -> r.end
      ).asJava
    )
  })
}

case class SimpleRange(start: Int, end: Int) {
  require(start <= end)
}