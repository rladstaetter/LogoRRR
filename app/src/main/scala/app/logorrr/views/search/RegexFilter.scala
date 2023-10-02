package app.logorrr.views.search

import javafx.scene.paint.Color

import java.util.regex.Pattern


class RegexFilter(override val pattern: String
                  , override val color: Color
                  , override val active: Boolean) extends Filter(pattern, color, active) {

  val compiledPattern = Pattern.compile(pattern)

  override def matches(searchTerm: String): Boolean = compiledPattern.matcher(searchTerm).find()

}

