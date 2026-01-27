package app.logorrr.cp

import scala.io.Source
import scala.util.{Try, Using}

case class TxtCp(classPathResource: String) extends CpResource:

  def asString(): Try[String] =
    Option(getClass.getResourceAsStream(classPathResource)) match
      case Some(inputStream) => Using(inputStream)(Source.fromInputStream(_).mkString)
      case None => throw new RuntimeException(s"Classpath resource not found: '$classPathResource'")

