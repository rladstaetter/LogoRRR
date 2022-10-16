package app.logorrr.views.text

import app.logorrr.views.search.Filter
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

object LogoRRRLabel {

  def mkBg(color: Color) = new Background(new BackgroundFill(color, new CornerRadii(1.25), Insets.EMPTY))

  def mkL(msg: String, color: Color): Label = {
    val l = new Label(msg)
    if (color != Filter.unClassifiedFilterColor) {
      l.setBackground(mkBg(color))
    }
    l
  }
}
