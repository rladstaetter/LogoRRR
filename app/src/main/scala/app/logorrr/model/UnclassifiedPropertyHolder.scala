package app.logorrr.model

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty}

trait UnclassifiedPropertyHolder:
  val unclassifiedProperty = new SimpleBooleanProperty()

  def setUnclassified(isUnclassified: Boolean): Unit = unclassifiedProperty.set(isUnclassified)

  def isUnclassified: Boolean = unclassifiedProperty.get()

  def bindUnclassifiedProperty(unclassifiedProperty: BooleanProperty): Unit = this.unclassifiedProperty.bind(unclassifiedProperty)

  def unbindUnclassfiedProperty(): Unit = unclassifiedProperty.unbind()
