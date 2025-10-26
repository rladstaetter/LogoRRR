package app.logorrr.views.menubar

import app.logorrr.io.FileId
import app.logorrr.views.a11y.uinodes.LogoRRRMenu
import app.logorrr.views.settings.SettingsEditor
import javafx.scene.control.MenuItem
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import javafx.stage.Stage
import net.ladstatt.util.log.CanLog

class SettingsMenuItem(stage: Stage) extends MenuItem("Settings...") with CanLog {
  setId(LogoRRRMenu.Settings.value)
  setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN))
  setOnAction(_ => new SettingsEditor(stage, FileId("")).showAndWait())
}
