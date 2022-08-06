package app.logorrr.views.search

import app.logorrr.views.search.Filter.LMatcher
import javafx.scene.paint.Color

import java.util.regex.Pattern


class RegexMatcher(pattern: String, colorString: String) extends LMatcher {

  val compiledPattern = Pattern.compile(pattern)

  override def color: Color = Color.web(colorString)

  override def applyMatch(searchTerm: String): Boolean = {
    val r = compiledPattern.matcher(searchTerm).find()
    println(r + " " + pattern + " " + searchTerm)
    r
  }
}

class RegexFilter(override val pattern: String
                  , override val colorString: String) extends Filter(pattern, colorString) {
  override val matcher: LMatcher = new RegexMatcher(pattern, colorString)
}