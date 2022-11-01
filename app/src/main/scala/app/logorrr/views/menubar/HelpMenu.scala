package app.logorrr.views.menubar

import app.logorrr.io.FilePaths
import app.logorrr.meta.AppMeta
import app.logorrr.views.about.AboutScreen
import app.logorrr.views.menubar.HelpMenu.{AboutMenuItem, LogMenuItem}
import javafx.scene.Scene
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{Modality, Stage}

import java.nio.file.Path

object HelpMenu {

  class LogMenuItem(openLogFile: Path => Unit) extends MenuItem("Open LogoRRRs log") {
    setOnAction(_ => {
      openLogFile(FilePaths.logFilePath)
    })
  }

  class AboutMenuItem extends MenuItem("About") {
    setOnAction(_ => {
      val stage = new Stage()
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle(s"About ${AppMeta.fullAppNameWithVersion}")
      val scene = new Scene(new AboutScreen, 440, 210)
      stage.setScene(scene)
      stage.setOnCloseRequest(_ => stage.close())
      stage.showAndWait()
    })
  }
}

class HelpMenu(openLogFile: Path => Unit) extends Menu("Help") {
  getItems.addAll(new LogMenuItem(openLogFile), new AboutMenuItem())
}