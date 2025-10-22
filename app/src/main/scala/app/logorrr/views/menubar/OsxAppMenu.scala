package app.logorrr.views.menubar

import app.logorrr.LogoRRRApp
import app.logorrr.views.about.AboutMenuItem
import de.jangassen.MenuToolkit
import javafx.scene.control.{Menu, MenuItem, SeparatorMenuItem}
import javafx.stage.Stage

/**
 * Menu only shown on OsX
 */
object OsxAppMenu {

  def mkMenu(stage: Stage, tk: MenuToolkit): Menu = {
    val aboutMenuItem = AboutMenuItem(tk.createNativeAboutMenuItem(LogoRRRApp.Name), stage)
    val hideMenuItem = tk.createHideMenuItem(LogoRRRApp.Name)
    val hideOthersMenuItem = tk.createHideOthersMenuItem
    val unhideOthersMenuItem: MenuItem = tk.createUnhideAllMenuItem
    val closeMenuItem = CloseApplicationMenuItem(tk.createQuitMenuItem(LogoRRRApp.Name), stage)
    val menu = new Menu(LogoRRRApp.Name)
    val prefsMenuItem = new SettingsMenuItem
    menu.getItems.addAll(aboutMenuItem
      , new SeparatorMenuItem
      , prefsMenuItem
      , new SeparatorMenuItem
      , hideMenuItem
      , hideOthersMenuItem
      , unhideOthersMenuItem
      , new SeparatorMenuItem
      , closeMenuItem)
    menu
  }

}
