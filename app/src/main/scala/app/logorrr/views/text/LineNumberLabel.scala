package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.util.LogoRRRFonts
import javafx.scene.control.Label

object LineNumberLabel {

  val size = 12

  def apply(lineNumber: Int, maxLength: Int): LineNumberLabel = {
    val ldl = new LineNumberLabel
    val string = lineNumber.toString.reverse.padTo(maxLength, " ").reverse.mkString
    ldl.setText(string)
    ldl
  }
}

class LineNumberLabel extends Label {
  // val c = Color.web("bisque")
  setStyle(LogoRRRFonts.jetBrainsMono(LineNumberLabel.size) + "-fx-background-color: BISQUE;")
  setText("")
}