package app.logorrr.jfxbfr

import javafx.scene.paint.Color

trait ColorChozzer[A] {

  def calc(a: A): Color

}
