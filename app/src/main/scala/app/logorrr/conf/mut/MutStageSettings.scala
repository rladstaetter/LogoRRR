package app.logorrr.conf.mut

import app.logorrr.conf.{SettingsIO, StageSettings}
import app.logorrr.util.JfxUtils
import app.logorrr.views.main.LG
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.stage.Window

object MutStageSettings {

  val windowListener = JfxUtils.onNew[Window](window => MutStageSettings.bind(window))

  private def updateSettings(updateFn: StageSettings => StageSettings): Unit = {
    val settings1 = SettingsIO.read()
    val newStageSettings = updateFn(settings1.stageSettings)
    SettingsIO.write(settings1.copy(stageSettings = newStageSettings))
  }

  val stageWidthListener =
    JfxUtils.onNew[Number](n => updateSettings(stageSettings => stageSettings.copy(width = n.intValue())))

  val stageHeightListener =
    JfxUtils.onNew[Number](n => updateSettings(stageSettings => stageSettings.copy(height = n.intValue())))

  val stageXListener =
    JfxUtils.onNew[Number](xValue => updateSettings(stageSettings => stageSettings.copy(x = xValue.doubleValue())))

  val stageYListener =
    JfxUtils.onNew[Number](yValue => updateSettings(stageSettings => stageSettings.copy(y = yValue.doubleValue())))

  def bind(window: Window): Unit = {
    LG.settings.stageSettings.widthProperty.bind(window.getScene.widthProperty())
    LG.settings.stageSettings.widthProperty.addListener(MutStageSettings.stageWidthListener)

    LG.settings.stageSettings.heightProperty.bind(window.getScene.heightProperty())
    LG.settings.stageSettings.heightProperty.addListener(MutStageSettings.stageHeightListener)

    LG.settings.stageSettings.xProperty.bind(window.xProperty())
    LG.settings.stageSettings.xProperty.addListener(MutStageSettings.stageXListener)

    LG.settings.stageSettings.yProperty.bind(window.yProperty())
    LG.settings.stageSettings.yProperty.addListener(MutStageSettings.stageYListener)
  }

  def unbind(): Unit = {
    LG.settings.stageSettings.widthProperty.unbind()
    LG.settings.stageSettings.widthProperty.removeListener(MutStageSettings.stageWidthListener)

    LG.settings.stageSettings.heightProperty.unbind()
    LG.settings.stageSettings.heightProperty.removeListener(MutStageSettings.stageHeightListener)

    LG.settings.stageSettings.xProperty.unbind()
    LG.settings.stageSettings.xProperty.removeListener(MutStageSettings.stageXListener)

    LG.settings.stageSettings.yProperty.unbind()
    LG.settings.stageSettings.yProperty.removeListener(MutStageSettings.stageYListener)
  }

  def apply(stageSettings: StageSettings): MutStageSettings = {
    val s = new MutStageSettings
    s.setX(stageSettings.x)
    s.setY(stageSettings.y)
    s.setWidth(stageSettings.width)
    s.setHeight(stageSettings.height)
    s
  }

}


class MutStageSettings extends Petrify[StageSettings] {


  val xProperty = new SimpleDoubleProperty()

  val yProperty = new SimpleDoubleProperty()

  val widthProperty = new SimpleIntegerProperty()

  val heightProperty = new SimpleIntegerProperty()

  def setX(x: Double): Unit = xProperty.set(x)

  def setY(y: Double): Unit = yProperty.set(y)

  def setWidth(width: Int): Unit = widthProperty.set(width)

  def setHeight(height: Int): Unit = heightProperty.set(height)


  override def petrify(): StageSettings =
    StageSettings(xProperty.get()
      , yProperty.get()
      , widthProperty.get()
      , heightProperty.get())

}