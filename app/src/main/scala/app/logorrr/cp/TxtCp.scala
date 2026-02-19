package app.logorrr.cp

import net.ladstatt.util.log.TinyLog

import scala.io.Source
import scala.util.{Failure, Try, Using}

case class TxtCp(classPathResource: String) extends CpResource with TinyLog:

  def asString(): Try[String] = {
    Option(getClass.getResourceAsStream(classPathResource)) match
      case Some(inputStream) => Using(inputStream)(Source.fromInputStream(_).mkString)
      case None =>
        val msg = s"Classpath resource not found: '$classPathResource'"
        logError(msg)
        Failure(new RuntimeException(msg))
  }

