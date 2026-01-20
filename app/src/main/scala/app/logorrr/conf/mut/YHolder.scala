package app.logorrr.conf.mut

import javafx.beans.binding.{DoubleBinding, DoubleExpression}
import javafx.beans.property.{DoublePropertyBase, ReadOnlyDoubleProperty, SimpleDoubleProperty}

trait YHolder:
  protected val yProperty = new SimpleDoubleProperty()

  def setY(y: Double): Unit = yProperty.set(y)

  def getY: Double = yProperty.get()

  def bindYProperty(yBinding: DoubleExpression): Unit = this.yProperty.bind(yBinding)

  def unbindYProperty(): Unit = yProperty.unbind()
