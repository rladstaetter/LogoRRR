package app.logorrr.views.util

import app.logorrr.clv.color.ColorUtil
import app.logorrr.clv.color.ColorUtil.cssLinearGradient
import javafx.beans.binding.{Bindings, ObjectBinding, StringBinding}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.paint.Color

import java.util.concurrent.Callable

object CssBindingUtil:

  def mkTextStyleCallable(colorProperty: ObjectPropertyBase[Color]): Callable[String] =
    () =>
      Option(colorProperty.get()) match
        case Some(color) =>
          s"""
             |-fx-font-weight: bold;
             |-fx-text-fill: ${ColorUtil.hexString(color)};
             |""".stripMargin
        case None =>
          ""

  def gradientStyle(colorProperty: ObjectPropertyBase[Color]): Callable[String] =
    () =>
      Option(colorProperty.get()) match
        case Some(color) =>
          s"""
             |-fx-border-width: 1pt;
             |-fx-border-radius: 5pt;
             |-fx-background-radius: 5pt;
             |-fx-border-color: ${cssLinearGradient("to bottom right", color.darker, color)};
             |-fx-background-color: ${cssLinearGradient("to bottom right", color, color.darker)};
             |""".stripMargin
        case None =>
          ""

  def mkTextStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(mkTextStyleCallable(colorProperty), colorProperty)

  def mkGradientStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(gradientStyle(colorProperty), colorProperty)


  def mkContrastPropertyBinding(colorProperty: ObjectPropertyBase[Color]): ObjectBinding[Color] = Bindings.createObjectBinding(
    () =>
      Option(colorProperty.get()) match
        case Some(value) => ColorUtil.getContrastColor(value)
        case None => Color.WHITE
    , colorProperty
  )
