package app.logorrr.jfxbfr

import javafx.scene.paint.Color

trait ColorMatcher {

  def matches(searchTerm: String): Boolean

  def getColor: Color
}
