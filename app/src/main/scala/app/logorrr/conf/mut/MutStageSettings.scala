package app.logorrr.conf.mut

import app.logorrr.conf.{LogoRRRGlobals, StageSettings}
import app.logorrr.util.JfxUtils
import javafx.beans.binding.DoubleExpression
import javafx.beans.property.{Property, ReadOnlyDoubleProperty}
import javafx.beans.value.ChangeListener
import javafx.stage.Window


/**
 * App wide singleton to store and load global settings.
 */
object MutStageSettings:

  val windowListener: ChangeListener[Window] = JfxUtils.onNew[Window]:
    window => Option(window).foreach(LogoRRRGlobals.bindWindow)


class MutStageSettings
  extends XHolder with YHolder
    with WidthHolder with HeightHolder:

  def bindWindowProperties(xBinding: ReadOnlyDoubleProperty
                           , yBinding: ReadOnlyDoubleProperty
                           , widthBinding: ReadOnlyDoubleProperty
                           , heightBinding: DoubleExpression): Unit = {
    bindXProperty(xBinding)
    bindYProperty(yBinding)
    bindWidthProperty(widthBinding)
    bindHeightProperty(heightBinding)
  }

  def unbindWindow(): Unit =
    unbindXProperty()
    unbindYProperty()
    unbindWidthProperty()
    unbindHeightProperty()

  def mkImmutable(): StageSettings = StageSettings(getX, getY, getWidth, getHeight)

  def set(stageSettings: StageSettings) : Unit =
    setX(stageSettings.x)
    setY(stageSettings.y)
    setHeight(stageSettings.height)
    setWidth(stageSettings.width)


  /* all observable values for this class */
  lazy val allProps: Set[Property[?]] =
    Set(xProperty
      , yProperty
      , widthProperty
      , heightProperty)

