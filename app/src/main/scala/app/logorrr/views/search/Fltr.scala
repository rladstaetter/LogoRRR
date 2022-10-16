package app.logorrr.views.search

import javafx.scene.paint.Color

trait Fltr {

  val color: Color

  def applyMatch(searchTerm: String): Boolean

}








