package app.logorrr.model

import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.scene.paint.Color

trait ColorPropertyHolder:
  val colorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()

  def getColor: Color = colorProperty.get()

  def setColor(color: Color): Unit = colorProperty.set(color)

  def bindColorProperty(colorProperty: ObjectPropertyBase[Color]): Unit = colorProperty.bind(colorProperty)

  def unbindColorProperty(): Unit = colorProperty.unbind()
