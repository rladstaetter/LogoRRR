package app.logorrr.views.logfiletab

import app.logorrr.views.ops.{DecreaseSizeButton, IncreaseSizeButton}
import javafx.beans.property.Property
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.{ListView, Slider}
import javafx.scene.layout.{BorderPane, HBox, Priority, VBox}

case class PaneDefinition(jfxId: String, graphic: Node, step: Int, boundary: Int)


class LogPartPane(listView: ListView[?]
                  , slider: Slider
                  , inc: PaneDefinition
                  , dec: PaneDefinition
                  , boundProp: Property[Number]) extends BorderPane:

  val increaseButton = IncreaseSizeButton(inc.jfxId, inc.graphic, inc.step, inc.boundary, boundProp)
  val decreaseButton = DecreaseSizeButton(dec.jfxId, dec.graphic, dec.step, dec.boundary, boundProp)
  val hbox = new HBox(slider, decreaseButton, increaseButton)
  HBox.setHgrow(slider, Priority.ALWAYS)
  hbox.setAlignment(Pos.CENTER)
  hbox.setPrefWidth(java.lang.Double.MAX_VALUE)
  VBox.setVgrow(this, Priority.ALWAYS)
  setMaxHeight(java.lang.Double.MAX_VALUE)
  setTop(hbox)
  setCenter(listView)

  def bind() : Unit =
    slider.valueProperty().bindBidirectional(boundProp)

  def unbind() : Unit =
    slider.valueProperty().unbindBidirectional(boundProp)