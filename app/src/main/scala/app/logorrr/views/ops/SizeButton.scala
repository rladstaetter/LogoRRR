package app.logorrr.views.ops

import app.logorrr.conf.FileId
import javafx.beans.binding.Bindings
import javafx.beans.property.{Property, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.scene.Node
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.input.{KeyCode, KeyEvent}

abstract class SizeButton(calcId: SimpleObjectProperty[FileId] => String
                          , node: Node
                          , step: Int
                          , boundary: Int
                          , fun: (Int, Int) => Int
                          , cmp: (Int, Int) => Boolean
                          , tooltipMessage: String) extends Button:

  val sizeProperty: Property[Number] = new SimpleIntegerProperty()

  def setSize(size: Number): Unit = sizeProperty.setValue(size)

  def getSize: Number = sizeProperty.getValue

  setTooltip(new Tooltip(tooltipMessage))
  setGraphic(node)
  setOnKeyPressed((event: KeyEvent) => {
    if event.getCode == KeyCode.ENTER then {
      fire()
    }
  })

  setOnAction:
    _ =>
      val nextSize = fun(getSize.intValue(), step)
      if cmp(nextSize, boundary) then setSize(nextSize) else setSize(boundary)


  def bind(fileIdProperty: SimpleObjectProperty[FileId], sizeProperty: Property[Number]): Unit =
    idProperty().bind(Bindings.createStringBinding(() => calcId(fileIdProperty), fileIdProperty))
    this.sizeProperty.bindBidirectional(sizeProperty)

  def unbind(sizeProperty: Property[Number]): Unit =
    idProperty.unbind()
    this.sizeProperty.unbindBidirectional(sizeProperty)

