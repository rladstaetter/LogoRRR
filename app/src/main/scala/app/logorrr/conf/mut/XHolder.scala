package app.logorrr.conf.mut

import javafx.beans.binding.DoubleExpression
import javafx.beans.property.{DoublePropertyBase, ReadOnlyDoubleProperty, SimpleDoubleProperty}

trait XHolder:
  protected val xProperty = new SimpleDoubleProperty()

  def setX(x: Double): Unit = xProperty.set(x)

  def getX: Double = xProperty.get()

  def bindXProperty(xBinding: DoubleExpression): Unit = this.xProperty.bind(xBinding)

  def unbindXProperty(): Unit = xProperty.unbind()
