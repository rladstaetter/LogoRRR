package app.logorrr.views.menubar

import app.logorrr.meta.AppMeta
import app.logorrr.views.about.AboutScreen
import app.logorrr.views.menubar.HelpMenu.AboutMenuItem
import javafx.application.HostServices
import javafx.scene.Scene
import javafx.scene.control.{Menu, MenuItem}
import javafx.stage.{Modality, Stage}

object HelpMenu {

  class AboutMenuItem(hostServices: HostServices) extends MenuItem("About") {
    setOnAction(_ => {
      val stage = new Stage()
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle(s"About ${AppMeta.fullAppName}")
      val scene = new Scene(new AboutScreen(hostServices), 440, 210)
      stage.setScene(scene)
      stage.setOnCloseRequest(_ => stage.close())
      stage.showAndWait()
    })
  }
}

class HelpMenu(hostServices: HostServices) extends Menu("Help") {
  getItems.add(new AboutMenuItem(hostServices))
}