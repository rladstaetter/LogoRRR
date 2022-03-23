package app.logorrr.conf.mut

import app.logorrr.conf.{SquareImageSettings, StageSettings}
import javafx.beans.property.SimpleIntegerProperty

object MutSquareImageSettings

class MutSquareImageSettings extends Petrify[SquareImageSettings] {

  val widthProperty = new SimpleIntegerProperty()

  def setWidth(width: Int): Unit = widthProperty.set(width)

  override def petrify(): SquareImageSettings = SquareImageSettings(widthProperty.get)
}