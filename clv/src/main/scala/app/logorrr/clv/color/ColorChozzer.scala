package app.logorrr.clv.color

import javafx.scene.paint.Color

trait ColorChozzer[A] {

  def calc(a: A): Color

}
