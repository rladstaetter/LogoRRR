package app.logorrr.cp

import scala.io.Source
import scala.util.{Try, Using}

case class TxtCp(classPathResource: String) extends CpResource {

  def asString(): Try[String] = {
    Using(getClass.getResourceAsStream(classPathResource)) { inputStream =>
      if (inputStream == null) {
        throw new RuntimeException(s"Resource not found: $classPathResource")
      }

      // Convert stream to String
      Source.fromInputStream(inputStream).mkString
    }
  }

}
