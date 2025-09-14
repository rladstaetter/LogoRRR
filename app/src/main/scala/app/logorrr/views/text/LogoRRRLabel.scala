package app.logorrr.views.text

import app.logorrr.views.SearchTerm
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

object LogoRRRLabel {

  private def mkBg(color: Color) = new Background(new BackgroundFill(color, new CornerRadii(1.25), Insets.EMPTY))

  def mkL(msg: String, color: Color): Label = {
    val l = new Label(msg)
    if (color != SearchTerm.Unclassified) {
      l.setBackground(mkBg(color))
    }
    l
  }
}
