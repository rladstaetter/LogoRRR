package app.logorrr.views.search

import javafx.scene.paint.Color

import java.util.regex.Pattern


class RegexFilter(override val pattern: String
                  , override val color: Color) extends Filter(pattern, color) {

  val compiledPattern = Pattern.compile(pattern)

  override def applyMatch(searchTerm: String): Boolean = compiledPattern.matcher(searchTerm).find()

}

