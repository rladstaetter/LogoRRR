package app.logorrr.views.ops

import app.logorrr.util.LogoRRRFonts
import javafx.scene.control.Label
import javafx.scene.paint.Color

object TextSizeButton {

  def mkLabel(fontSize:Int) : Label = {
    val label = new Label("T")
    label.setStyle(LogoRRRFonts.jetBrainsMono(fontSize))
    label.setTextFill(Color.DARKGREY)
    label
  }

}


