package app.logorrr.views.ops

import javafx.beans.property.{Property, SimpleIntegerProperty}
import javafx.scene.Node
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.input.{KeyCode, KeyEvent}

abstract class SizeButton(node: Node
                          , tooltipMessage: String) extends Button {

  val sizeProperty: Property[Number] = new SimpleIntegerProperty()

  def setSize(size: Number): Unit = sizeProperty.setValue(size)

  def getSize: Number = sizeProperty.getValue

  setTooltip(new Tooltip(tooltipMessage))
  setGraphic(node)
  setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      fire()
    }
  })

}
