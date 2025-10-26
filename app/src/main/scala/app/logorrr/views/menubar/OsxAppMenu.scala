package app.logorrr.views.menubar

import app.logorrr.LogoRRRApp
import app.logorrr.views.a11y.uinodes.LogoRRRMenu
import app.logorrr.views.about.AboutMenuItem
import de.jangassen.MenuToolkit
import javafx.scene.control.{Menu, MenuItem, SeparatorMenuItem}
import javafx.stage.Stage

/**
 * Menu only shown on OsX
 */
object OsxAppMenu {

  def mkMenu(stage: Stage, tk: MenuToolkit, isUnderTest: Boolean): Menu = {
    val aboutMenuItem = AboutMenuItem(tk.createNativeAboutMenuItem(LogoRRRApp.Name), stage)
    val hideMenuItem = tk.createHideMenuItem(LogoRRRApp.Name)
    val hideOthersMenuItem = tk.createHideOthersMenuItem
    val unhideOthersMenuItem: MenuItem = tk.createUnhideAllMenuItem
    val closeMenuItem = CloseApplicationMenuItem(tk.createQuitMenuItem(LogoRRRApp.Name), stage, isUnderTest)
    val menu = new Menu(LogoRRRApp.Name)
    menu.setId(LogoRRRMenu.Self.value)
    val settingsMenuitem = new SettingsMenuItem(stage)
    menu.getItems.addAll(aboutMenuItem
      , new SeparatorMenuItem
      , settingsMenuitem
      , new SeparatorMenuItem
      , hideMenuItem
      , hideOthersMenuItem
      , unhideOthersMenuItem
      , new SeparatorMenuItem
      , closeMenuItem)
    menu
  }

}
