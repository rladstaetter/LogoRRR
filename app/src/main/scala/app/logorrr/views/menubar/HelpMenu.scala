package app.logorrr.views.menubar

import app.logorrr.io.FileId
import app.logorrr.meta.AppInfo
import app.logorrr.views.a11y.UiNodes
import app.logorrr.views.about.AboutDialog
import app.logorrr.views.menubar.HelpMenu.{AboutMenuItem, LogMenuItem}
import javafx.scene.Scene
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{Modality, Stage}
import net.ladstatt.util.log.CanLog

object HelpMenu extends CanLog {

  class LogMenuItem(openLogFile: FileId => Unit) extends MenuItem("Open LogoRRRs log") {
    setId(UiNodes.HelpMenu.OpenLogorrLog.value)

    setOnAction(_ => {
      openLogFile(FileId(logFilePath))
    })
  }

  class AboutMenuItem extends MenuItem("About") {
    setId(UiNodes.HelpMenu.About.value)

    setOnAction(_ => {
      val stage = new Stage()
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle(s"About ${AppInfo.fullAppNameWithVersion}")
      val scene = new Scene(new AboutDialog, 440, 250)
      stage.setScene(scene)
      stage.setOnCloseRequest(_ => stage.close())
      stage.showAndWait()
    })
  }
}

class HelpMenu(openFile: FileId => Unit) extends Menu("Help") {
  setId(UiNodes.HelpMenu.Self.value)
  getItems.addAll(new LogMenuItem(openFile), new AboutMenuItem())
}