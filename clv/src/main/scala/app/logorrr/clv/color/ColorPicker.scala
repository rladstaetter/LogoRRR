package app.logorrr.clv.color

import javafx.scene.paint.Color

trait ColorPicker[A]:

  def init(): Unit

  def calc(a: A): Color

