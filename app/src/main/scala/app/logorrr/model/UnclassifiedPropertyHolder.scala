package app.logorrr.model

import javafx.beans.property.SimpleBooleanProperty

trait UnclassifiedPropertyHolder:
  private val unclassifiedProperty = new SimpleBooleanProperty()

  def setUnclassified(isUnclassified: Boolean): Unit = unclassifiedProperty.set(isUnclassified)

  def isUnclassified: Boolean = unclassifiedProperty.get()
