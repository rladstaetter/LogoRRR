package app.logorrr.views.logfiletab

import app.logorrr.conf.FileId
import app.logorrr.views.ops.{DecreaseSizeButton, IncreaseSizeButton}
import javafx.beans.property.{Property, SimpleObjectProperty}
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.{ListView, Slider}
import javafx.scene.layout.{BorderPane, HBox, Priority, VBox}


class LogPartPane(listView: ListView[?]
                  , slider: Slider
                  , inc: PaneDefinition
                  , dec: PaneDefinition
                  , boundProp: Property[Number]) extends BorderPane:

  val increaseButton = IncreaseSizeButton(inc.calcId, inc.graphic, inc.step, inc.boundary)
  val decreaseButton = DecreaseSizeButton(dec.calcId, dec.graphic, dec.step, dec.boundary)
  val hbox = new HBox(slider, decreaseButton, increaseButton)
  HBox.setHgrow(slider, Priority.ALWAYS)
  hbox.setAlignment(Pos.CENTER)
  hbox.setPrefWidth(java.lang.Double.MAX_VALUE)
  VBox.setVgrow(this, Priority.ALWAYS)
  setMaxHeight(java.lang.Double.MAX_VALUE)
  setTop(hbox)
  setCenter(listView)

  def bind(fileProperty: SimpleObjectProperty[FileId], boundProp: Property[Number]): Unit = {
    increaseButton.bind(fileProperty, boundProp)
    decreaseButton.bind(fileProperty, boundProp)
    slider.valueProperty().bindBidirectional(boundProp)
  }

  def unbind(): Unit = {
    increaseButton.unbind(boundProp)
    decreaseButton.unbind(boundProp)
    slider.valueProperty().unbindBidirectional(boundProp)
  }