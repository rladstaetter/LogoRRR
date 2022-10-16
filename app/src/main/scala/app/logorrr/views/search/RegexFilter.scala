package app.logorrr.views.search

import java.util.regex.Pattern


class RegexFilter(override val pattern: String
                  , override val colorString: String) extends Filter(pattern, colorString) {

  val compiledPattern = Pattern.compile(pattern)

  override def applyMatch(searchTerm: String): Boolean = compiledPattern.matcher(searchTerm).find()

}

