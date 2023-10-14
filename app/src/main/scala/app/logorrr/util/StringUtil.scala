package app.logorrr.util

import java.nio.charset.StandardCharsets

object StringUtil {

  val utf8: String = StandardCharsets.UTF_8.name()

  /** handy for conversions where T has a string representation. if f returns null, a 'n/a' string will be displayed */
  def tOrNa[T](f: => T): String = Option(f) match {
    case Some(value) => value.toString
    case None => "n/a"
  }

}
