package app.logorrr.conf.mut

import app.logorrr.conf.BlockSettings
import javafx.beans.property.SimpleIntegerProperty

object MutBlockSettings

class MutBlockSettings extends Petrify[BlockSettings] {

  val widthProperty = new SimpleIntegerProperty()

  def setWidth(width: Int): Unit = widthProperty.set(width)

  override def petrify(): BlockSettings = BlockSettings(widthProperty.get)
}