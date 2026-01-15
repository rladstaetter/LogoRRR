package app.logorrr.views.menubar

import app.logorrr.LogoRRRApp
import app.logorrr.conf.FileId
import app.logorrr.views.about.AboutMenuItem
import app.logorrr.views.menubar.HelpMenu.LogMenuItem
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.Stage
import net.ladstatt.util.log.TinyLog
import net.ladstatt.util.os.OsUtil

object HelpMenu:

  class LogMenuItem(openLogFile: FileId => Unit) extends MenuItem("Open LogoRRRs log"):
    setId(app.logorrr.views.a11y.uinodes.HelpMenu.OpenLogorrLog.value)
    setOnAction(_ => openLogFile(FileId(LogoRRRApp.paths.logFile)))


class HelpMenu(stage: Stage, openFile: FileId => Unit) extends Menu("Help"):
  setId(app.logorrr.views.a11y.uinodes.HelpMenu.Self.value)

  val items: Seq[MenuItem] =
    Seq(new LogMenuItem(openFile)) ++
      (if !OsUtil.isMac then {
        Seq(AboutMenuItem(new MenuItem("About"), stage))
      } else {
        Seq()
      })
  getItems.addAll(items *)

