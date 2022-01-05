package app.logorrr.util

import java.io.InputStream

abstract class CpResource(value: String) {

  /**
   * returns a inputstream handle to this classpath address
   *
   * @param clazz class whose classloader is used to load the resource
   * @return
   */
  def inputStream(clazz: Class[_]): InputStream = {
    val r = clazz.getResourceAsStream(value)
    require(Option(r).isDefined, s"'$value' does not exist.")
    r
  }

}
