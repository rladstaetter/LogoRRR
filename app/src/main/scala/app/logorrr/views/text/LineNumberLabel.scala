package app.logorrr.views.text

import javafx.scene.control.Label

object LineNumberLabel {


  def apply( lineNumber: Int, maxLength: Int): LineNumberLabel = {
    val ldl = new LineNumberLabel
    val string = lineNumber.toString.reverse.padTo(maxLength, " ").reverse.mkString
    ldl.setText(string)
    ldl
  }
}

class LineNumberLabel extends Label("")