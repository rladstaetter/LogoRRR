package app.logorrr.model

import javafx.beans.property.SimpleStringProperty

trait ValuePropertyHolder:
  val valueProperty: SimpleStringProperty = new SimpleStringProperty()

  def getValue: String = valueProperty.get()

  def setValue(value: String): Unit = valueProperty.set(value)

  def bindValueProperty(valueProperty: SimpleStringProperty): Unit = this.valueProperty.bind(valueProperty)

  def unbindValueProperty(): Unit = valueProperty.unbind()
