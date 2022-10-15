package app.logorrr.views.text

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill}
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
  setPadding(new Insets(0, 10, 0, 0))
  setBackground(new Background(new BackgroundFill(Color.FLORALWHITE, null, null)))
}