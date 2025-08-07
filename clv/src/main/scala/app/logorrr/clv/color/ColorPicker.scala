package app.logorrr.clv.color

import javafx.scene.paint.Color

trait ColorPicker[A] {

  def calc(a: A): Color

}
