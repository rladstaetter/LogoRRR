package app.logorrr.views.util

import app.logorrr.clv.color.ColorUtil
import app.logorrr.clv.color.ColorUtil.cssLinearGradient
import javafx.beans.binding.{Bindings, ObjectBinding, StringBinding}
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
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

  def mkRemoveSearchTermStyleCallable(colorProperty: ObjectPropertyBase[Color]): Callable[String] =
    () =>
      Option(colorProperty.get()) match
        case Some(color) =>
          s"""
             |-fx-padding: 0;
             |-fx-background-color: inherit;
             |-fx-text-fill: ${ColorUtil.hexString(color)};
             |""".stripMargin
        case None =>
          ""




  def gradientStyle(colorProperty: ObjectPropertyBase[Color]): Callable[String] =
    () =>
      Option(colorProperty.get()) match
        case Some(color) =>
          s"""
             |-fx-background-color: ${cssLinearGradient(color, color.darker)}
             |""".stripMargin
        case None =>
          ""

  def mkTextStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(mkTextStyleCallable(colorProperty), colorProperty)

  def mkRemoveSearchTermStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(mkRemoveSearchTermStyleCallable(colorProperty), colorProperty)

  def mkGradientStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(gradientStyle(colorProperty), colorProperty)


  def mkContrastPropertyBinding(colorProperty: ObjectPropertyBase[Color]): ObjectBinding[Color] = Bindings.createObjectBinding(
    () =>
      Option(colorProperty.get()) match
        case Some(value) => ColorUtil.getContrastColor(value)
        case None => Color.WHITE
    , colorProperty
  )
