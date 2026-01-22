package app.logorrr.conf.mut

import javafx.beans.property.{IntegerProperty, IntegerPropertyBase, SimpleIntegerProperty}

trait EndColHolder:
  val endColProperty: IntegerProperty = new SimpleIntegerProperty()

  def getEndCol: Integer = endColProperty.get()

  def setEndCol(endCol: Integer): Unit = endColProperty.set(endCol)

  def bindEndColProperty(endColProperty: IntegerPropertyBase): Unit = endColProperty.bind(endColProperty)

  def unbindEndColProperty(): Unit = endColProperty.unbind()
