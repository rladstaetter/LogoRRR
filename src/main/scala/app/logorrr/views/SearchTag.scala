package app.logorrr.views

import javafx.scene.control.{Button, ToggleButton}
import javafx.scene.layout.HBox

/**
 * Groups a toggle button to activate a filter and a button to remove it
 */
class SearchTag(val toggleButton: ToggleButton, val closeButton: Button) extends HBox {
  getChildren.addAll(toggleButton, closeButton)
}
