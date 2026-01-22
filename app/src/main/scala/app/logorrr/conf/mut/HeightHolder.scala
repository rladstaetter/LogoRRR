package app.logorrr.conf.mut

import javafx.beans.binding.{DoubleBinding, DoubleExpression}
import javafx.beans.property.{IntegerPropertyBase, ReadOnlyDoubleProperty, SimpleIntegerProperty}

trait HeightHolder:
  protected val heightProperty = new SimpleIntegerProperty()

  def setHeight(height: Int): Unit = heightProperty.set(height)

  def getHeight: Integer = heightProperty.get()

  def bindHeightProperty(heightBinding: DoubleExpression): Unit = heightProperty.bind(heightBinding)

  def unbindHeightProperty(): Unit = heightProperty.unbind()
