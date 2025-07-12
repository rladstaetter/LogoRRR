package app.logorrr.jfxbfr.color

import javafx.scene.paint.Color

trait ColorChozzer[A] {

  def calc(a: A): Color

}
