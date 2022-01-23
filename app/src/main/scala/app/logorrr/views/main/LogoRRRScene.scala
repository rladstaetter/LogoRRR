package app.logorrr.views.main

import app.logorrr.conf.{Settings, SettingsIO, StageSettings}
import app.logorrr.util.JfxUtils
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.stage.Window

object LogoRRRScene {

  private def updateSettings(updateFn: StageSettings => StageSettings): Unit = {
    val settings1 = SettingsIO.read()
    val newStageSettings = updateFn(settings1.stageSettings)
    SettingsIO.write(settings1.copy(stageSettings = newStageSettings))
  }

  private val stageWidthListener =
    JfxUtils.onNew[Number](n => updateSettings(stageSettings => stageSettings.copy(width = n.intValue())))

  private val stageHeightListener =
    JfxUtils.onNew[Number](n => updateSettings(stageSettings => stageSettings.copy(height = n.intValue())))

  private val stageXListener =
    JfxUtils.onNew[Number](xValue => updateSettings(stageSettings => stageSettings.copy(x = xValue.doubleValue())))

  private val stageYListener =
    JfxUtils.onNew[Number](yValue => updateSettings(stageSettings => stageSettings.copy(y = yValue.doubleValue())))

  def addWindowListeners(window: Window): Unit = {
    window.xProperty().addListener(stageXListener)
    window.yProperty().addListener(stageYListener)
    window.widthProperty().addListener(stageWidthListener)
    window.heightProperty().addListener(stageHeightListener)
  }

  def removeWindowListeners(window: Window): Unit = {
    window.xProperty().removeListener(stageXListener)
    window.yProperty().removeListener(stageYListener)
    window.widthProperty().removeListener(stageWidthListener)
    window.heightProperty().removeListener(stageHeightListener)
  }


  /**
   * @param x x coordinate of upper left corner of scene from last execution
   * @param y y coordinate of upper left corner of scene from last execution
   */
  def mkSceneListener(x: Double, y: Double)(): ChangeListener[Scene] =
    JfxUtils.onNew[Scene](scene => {
      scene.getWindow.setX(x)
      scene.getWindow.setY(y)
      addWindowListeners(scene.getWindow)
    })
}

case class LogoRRRScene(settings: Settings
                        , mainPane: LogoRRRMain)
  extends Scene(mainPane, settings.stageSettings.width, settings.stageSettings.height)
