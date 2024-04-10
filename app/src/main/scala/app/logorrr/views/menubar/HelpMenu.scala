package app.logorrr.views.menubar

import app.logorrr.io.{FileId, FilePaths}
import app.logorrr.meta.AppMeta
import app.logorrr.views.UiNodes
import app.logorrr.views.about.AboutScreen
import app.logorrr.views.menubar.HelpMenu.{AboutMenuItem, LogMenuItem}
import javafx.scene.Scene
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{Modality, Stage}

object HelpMenu {

  class LogMenuItem(openLogFile: FileId => Unit) extends MenuItem("Open LogoRRRs log") {
    setId(UiNodes.HelpMenuOpenLogorrLog.value)

    setOnAction(_ => {
      openLogFile(FileId(FilePaths.logFilePath))
    })
  }

  class AboutMenuItem extends MenuItem("About") {
    setId(UiNodes.HelpMenuAbout.value)

    setOnAction(_ => {
      val stage = new Stage()
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle(s"About ${AppMeta.fullAppNameWithVersion}")
      val scene = new Scene(new AboutScreen, 440, 250)
      stage.setScene(scene)
      stage.setOnCloseRequest(_ => stage.close())
      stage.showAndWait()
    })
  }
}

class HelpMenu(openFile: FileId => Unit) extends Menu("Help") {
  setId(UiNodes.HelpMenu.value)
  getItems.addAll(new LogMenuItem(openFile), new AboutMenuItem())
}