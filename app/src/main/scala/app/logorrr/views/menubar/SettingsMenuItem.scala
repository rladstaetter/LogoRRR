package app.logorrr.views.menubar

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.uinodes.LogoRRRMenu
import app.logorrr.views.settings.SettingsEditor
import javafx.scene.control.MenuItem
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

class SettingsMenuItem(stage: Stage) extends MenuItem("Settings...") with TinyLog:
  setId(LogoRRRMenu.Settings.value)
  if OsUtil.isMac then
    setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN))
  else
    setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN))
  setOnAction(_ => new SettingsEditor(stage, FileId("")).showAndWait())
