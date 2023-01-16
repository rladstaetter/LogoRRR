package app.logorrr.util

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill}
import javafx.scene.paint.Color

object LabelUtil {

  def setStyle(label: Label, textFill: Color, padding: Insets, backgroundColor: Color): Unit = {
    label.setTextFill(textFill)
    label.setPadding(padding)
    label.setBackground(new Background(new BackgroundFill(backgroundColor, null, null)))
  }

  def resetStyle(label: Label): Unit = {
    label.setBackground(null)
  }
}
