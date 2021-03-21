package net.ladstatt.logorrr.views

import javafx.scene.control.{Button, ToggleButton}
import javafx.scene.layout.HBox

class SearchTag(val toggleButton: ToggleButton, val closeButton: Button) extends HBox {
  getChildren.addAll(toggleButton, closeButton)
}
