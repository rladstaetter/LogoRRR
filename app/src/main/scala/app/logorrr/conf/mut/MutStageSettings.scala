package app.logorrr.conf.mut

import app.logorrr.conf.{LogoRRRGlobals, StageSettings}
import app.logorrr.util.JfxUtils
import javafx.beans.binding.{DoubleBinding, DoubleExpression}
import javafx.beans.property.{DoublePropertyBase, IntegerPropertyBase, ReadOnlyDoubleProperty, SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.value.ChangeListener
import javafx.stage.Window

/**
 * App wide singleton to store and load global settings.
 */
object MutStageSettings:

  val windowListener: ChangeListener[Window] = JfxUtils.onNew[Window]:
    window =>
      Option(window).foreach(LogoRRRGlobals.bindWindow)


class MutStageSettings
  extends XHolder with YHolder
    with WidthHolder with HeightHolder:

  def bind(xBinding: DoubleExpression
           , yBinding: DoubleExpression
           , widthBinding: DoubleExpression
           , heightBinding: DoubleExpression): Unit = {
    bindXProperty(xBinding)
    bindYProperty(yBinding)
    bindWidthProperty(widthBinding)
    bindHeightProperty(heightBinding)
  }

  def unbind(): Unit =
    unbindXProperty()
    unbindYProperty()
    unbindWidthProperty()
    unbindHeightProperty()

  def mkImmutable(): StageSettings = StageSettings(getX, getY, getWidth, getHeight)

