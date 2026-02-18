package app.logorrr.model

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty}

trait ActivePropertyHolder:
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  def setActive(active: Boolean): Unit = activeProperty.set(active)

  def isActive: Boolean = activeProperty.get()

  def bindActiveProperty(activeProperty: BooleanProperty): Unit = this.activeProperty.bind(activeProperty)

  def bindBidirectionalActiveProperty(other: BooleanProperty): Unit = this.activeProperty.bindBidirectional(other)

  def unbindActiveProperty(): Unit = activeProperty.unbind()

  def unbindBidirectionalActiveProperty(other: BooleanProperty): Unit = this.activeProperty.unbindBidirectional(other)
