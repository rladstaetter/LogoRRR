package app.logorrr.views.search

import javafx.scene.paint.Color

abstract class Fltr(val color: Color) {
  def matches(searchTerm: String): Boolean
}








