package app.logorrr.views.search

import app.logorrr.jfxbfr.Fltr
import javafx.scene.paint.Color

import java.util.regex.Pattern

case class RegexPredicate(pattern: String) extends Function1[String,Boolean] {
  val compiledPattern: Pattern = Pattern.compile(pattern)
  override def apply(searchTerm: String): Boolean =  compiledPattern.matcher(searchTerm).find()

}

case class RegexFilter(pattern: String
                       , color: Color
                       , active: Boolean) extends Fltr {

  init(RegexPredicate(pattern), pattern, color, active)

  val compiledPattern: Pattern = Pattern.compile(pattern)

  override def matches(searchTerm: String): Boolean = compiledPattern.matcher(searchTerm).find()

}

