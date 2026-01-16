package app.logorrr.views.util

import app.logorrr.clv.color.ColorUtil
import app.logorrr.clv.color.ColorUtil.cssLinearGradient
import javafx.beans.binding.{Bindings, ObjectBinding, StringBinding}
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.scene.paint.Color

import java.util.concurrent.Callable

object CssBindingUtil:

  def mkTextStyleCallable(colorProperty: ObjectPropertyBase[Color]): Callable[String] = () =>
    s"""
       |-fx-font-weight: bold;
       |-fx-text-fill: ${ColorUtil.hexString(colorProperty.get)};
       |""".stripMargin

  def gradientStyle(colorProperty: ObjectPropertyBase[Color]): Callable[String] = () =>
    s"""
       |-fx-background-color: ${cssLinearGradient(colorProperty.get, colorProperty.get.darker)}
       |""".stripMargin

  def mkTextStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(mkTextStyleCallable(colorProperty), colorProperty)

  def mkGradientStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(gradientStyle(colorProperty), colorProperty)


  def mkContrastPropertyBinding(colorProperty: ObjectPropertyBase[Color]): ObjectBinding[Color] = Bindings.createObjectBinding(
    () =>
      Option(colorProperty.get()) match
        case Some(value) => ColorUtil.getContrastColor(colorProperty.get())
        case None => Color.WHITE
    , colorProperty
  )
