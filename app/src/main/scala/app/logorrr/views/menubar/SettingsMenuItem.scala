package app.logorrr.views.menubar

import app.logorrr.model.OpenSettingsEditorEvent
import app.logorrr.views.a11y.uinodes.LogoRRRMenu
import app.logorrr.views.main.LogoRRRMain
import javafx.scene.control.MenuItem
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

class SettingsMenuItem(main: LogoRRRMain) extends MenuItem("Settings...") with TinyLog:
  setId(LogoRRRMenu.Settings.value)
  if OsUtil.isMac then
    setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN))
  else
    setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN))
  setOnAction(_ => main.fireEvent(OpenSettingsEditorEvent()))
