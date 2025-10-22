package app.logorrr.views.menubar

import javafx.scene.control.MenuItem
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}

class SettingsMenuItem extends MenuItem("Settings...") {
  setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN))
  setOnAction(_ => {
    println("Opening settings dialog")
  })
}
