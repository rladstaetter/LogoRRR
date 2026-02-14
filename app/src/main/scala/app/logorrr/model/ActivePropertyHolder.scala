package app.logorrr.model

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty}

trait ActivePropertyHolder:
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  def setActive(active: Boolean): Unit = activeProperty.set(active)

  def isActive: Boolean = activeProperty.get()

  def bindActiveProperty(activeProperty: BooleanProperty): Unit = activeProperty.bind(activeProperty)

  def unbindActiveProperty(): Unit = activeProperty.unbind()
