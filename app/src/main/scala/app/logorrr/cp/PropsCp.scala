package app.logorrr.cp

import java.util.Properties

case class PropsCp(classPathResource: String) extends CpResource {

  def asProperties(clazz: Class[?]): Properties = {
    val properties = new Properties()
    val is = clazz.getResourceAsStream(classPathResource)
    try {
      properties.load(is)
    } finally {
      Option(is).foreach(_.close)
    }
    properties
  }

}
