package app.logorrr

import app.logorrr.FileMenu.{CloseAllMenuItem, OpenMenuItem, OpenRecentMenu}
import app.logorrr.HelpMenu.AboutMenuItem
import app.logorrr.conf.Settings
import app.logorrr.util.OsUtil
import app.logorrr.views.about.AboutScreen
import javafx.application.HostServices
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}
import javafx.scene.layout.BorderPane
import javafx.stage.{Modality, Stage, WindowEvent}

import java.nio.file.{Files, Path, Paths}

object HelpMenu {

  class AboutMenuItem(hostServices: HostServices) extends MenuItem("About") {
    setOnAction(_ => {
      val stage = new Stage()
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle("About " + Settings.fullAppName)
      val scene = new Scene(new AboutScreen(hostServices), 440,210)
      stage.setScene(scene)
      stage.setOnCloseRequest(_ => stage.close())
      stage.showAndWait()
    })
  }
}

class HelpMenu(hostServices: HostServices) extends Menu("Help") {
  getItems.add(new AboutMenuItem(hostServices))
}

object FileMenu {

  object OpenMenuItem extends MenuItem("Open")

  object QuitMenuItem extends MenuItem("Quit") {

  }

  object OpenRecentMenu {

    class RecentFileMenuItem(path: Path) extends MenuItem(path.getFileName.toString)

    object RemoveAllRecentFilesMenuItem extends MenuItem("Remove all")

    class OpenRecentMenu extends Menu("Open Recent")

    def menu(recentFiles: Seq[Path]): Menu = {
      val m = new OpenRecentMenu
      m.getItems.addAll(recentFiles.map(p => new OpenRecentMenu.RecentFileMenuItem(p)): _*)
      m.getItems.add(new SeparatorMenuItem())
      m.getItems.add(OpenRecentMenu.RemoveAllRecentFilesMenuItem)
      m
    }
  }

  object CloseAllMenuItem extends MenuItem("Close All")

  private def mkFileMenu(settings: Settings) = {
    val fm = new Menu("File")
    fm.getItems.add(OpenMenuItem)
    if (settings.recentFiles.files.nonEmpty) {
      fm.getItems.add(OpenRecentMenu.menu(settings.recentFiles.files.map(f => Paths.get(f)).filter(p => Files.exists(p))))
    }
    fm.getItems.add(CloseAllMenuItem)
    fm
  }


}

class FileMenu(settings: Settings) extends Menu("File") {

  def init(): Unit = {
    getItems.add(OpenMenuItem)
    if (settings.recentFiles.files.nonEmpty) {
      getItems.add(OpenRecentMenu.menu(settings.recentFiles.files.map(f => Paths.get(f)).filter(p => Files.exists(p))))
    }
    getItems.add(CloseAllMenuItem)
    if (OsUtil.isWin) {
      getItems.add(FileMenu.QuitMenuItem)
    }
  }

  init()
}

class LogoRRRMenuBar(hostServices: HostServices, settings: Settings) extends MenuBar {
  def init(): Unit = {
    if (OsUtil.isMac) {
      setUseSystemMenuBar(OsUtil.isMac)
    }
    getMenus.addAll(new FileMenu(settings), new HelpMenu(hostServices))
  }

  init()
}

class LogorrrMainPane(hostServices: HostServices
                      , settings: Settings) extends BorderPane {

  val ambp = new AppMainBorderPane(settings.stageSettings.width, settings.squareImageSettings.width)

  val mB = new LogoRRRMenuBar(hostServices, settings)

  setTop(mB)
  setCenter(ambp)

  def setSceneWidth(sceneWidth: Int): Unit = ambp.setSceneWidth(sceneWidth)

  def addLogFile(p: Path): Unit = ambp.addLogFile(p)

  def selectLastLogFile(): Unit = ambp.selectLastLogFile()

  def shutdown(): Unit = ambp.shutdown()

}

object LogoRRRAppBuilder {

  def withStage(stage: Stage
                , params: Seq[String]
                , settings: Settings
                , hs: HostServices): Stage = {
    stage.setTitle(Settings.fullAppName)
    stage.getIcons.add(Settings.icon)
    val abbp = new LogorrrMainPane(hs, settings)
    val scene = new Scene(abbp, settings.stageSettings.width, settings.stageSettings.height)
    scene.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        Option(abbp).foreach(_.setSceneWidth(t1.intValue))
      }
    })
    stage.setScene(scene)

    for (p <- params) {
      abbp.addLogFile(Paths.get(p).toAbsolutePath)
    }
    abbp.selectLastLogFile()

    // make sure to cleanup on close
    stage.setOnCloseRequest((_: WindowEvent) => abbp.shutdown())
    stage
  }
}
