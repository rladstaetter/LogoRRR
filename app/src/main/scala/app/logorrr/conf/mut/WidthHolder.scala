package app.logorrr.conf.mut

import javafx.beans.binding.{DoubleBinding, DoubleExpression}
import javafx.beans.property.{IntegerPropertyBase, ReadOnlyDoubleProperty, SimpleIntegerProperty}

trait WidthHolder:
  protected val widthProperty = new SimpleIntegerProperty()

  def setWidth(width: Int): Unit = widthProperty.set(width)

  def getWidth: Int = widthProperty.get()

  def bindWidthProperty(widthBinding: DoubleExpression): Unit = this.widthProperty.bind(widthBinding)

  def unbindWidthProperty(): Unit = widthProperty.unbind()
