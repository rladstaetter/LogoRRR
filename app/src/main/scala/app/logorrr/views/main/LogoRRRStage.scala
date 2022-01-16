package app.logorrr.views.main

import app.logorrr.conf.{AppMeta, Settings}
import app.logorrr.util.JfxUtils
import javafx.application.HostServices
import javafx.scene.image.Image
import javafx.stage.{Stage, WindowEvent}


case class LogoRRRStage(stage: Stage
                        , settings: Settings
                        , hs: HostServices) {

  val icon: Image = new Image(getClass.getResourceAsStream("/app/logorrr/icon/logorrr-icon-32.png"))
  val mainPane = new LogoRRRMain(hs, closeStage(), settings)
  val scene = LogoRRRScene(settings, mainPane)
  val sceneListener = LogoRRRScene.mkSceneListener(settings.stageSettings.x, settings.stageSettings.y)()
  val abbWidthListener = JfxUtils.onNew[Number](width => mainPane.setSceneWidth(width.intValue))

  /** selected via File Menu on Windows */
  def closeStage(): Unit = {
    stage.fireEvent(
      new WindowEvent(
        stage,
        WindowEvent.WINDOW_CLOSE_REQUEST
      )
    )
  }

  def show(): Unit = {
    scene.widthProperty().addListener(abbWidthListener)
    stage.sceneProperty().addListener(sceneListener)
    stage.setTitle(AppMeta.fullAppName)
    stage.getIcons.add(icon)
    stage.setScene(scene)

    // make sure to cleanup on close
    stage.setOnCloseRequest((_: WindowEvent) => {
      mainPane.shutdown()
      LogoRRRScene.removeWindowListeners(scene.getWindow)
      scene.widthProperty().removeListener(abbWidthListener)
      stage.sceneProperty.removeListener(sceneListener)
    })
    stage.show()
  }
}
