package app.logorrr.views.text

import javafx.scene.control.Label
import javafx.scene.paint.Color

object LineNumberLabel {


  def apply(lineNumber: Int, maxLength: Int): LineNumberLabel = {
    val ldl = new LineNumberLabel
    val string = lineNumber.toString.reverse.padTo(maxLength, " ").reverse.mkString
    ldl.setText(string)
    ldl
  }
}

class LineNumberLabel extends Label("") {
  setTextFill(Color.SLATEGREY)
}