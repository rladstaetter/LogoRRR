package app.logorrr.views.util

import app.logorrr.clv.color.ColorUtil
import app.logorrr.clv.color.ColorUtil.cssLinearGradient
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.binding.{Bindings, ObjectBinding, StringBinding}
import javafx.beans.property.{BooleanProperty, ObjectPropertyBase}
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

  def gradientStyle(booleanProperty: BooleanProperty, colorProperty: ObjectPropertyBase[Color]): Callable[String] =
    () =>
      if (booleanProperty.get())
        Option(colorProperty.get()) match
          case Some(color) =>
            s"""
               |-fx-border-width: 1pt;
               |-fx-border-radius: 5pt;
               |-fx-background-radius: 5pt;
               |-fx-border-color: ${cssLinearGradient("to right", color.darker, color)};
               |-fx-background-color: ${cssLinearGradient("to right", color, color.darker)};
               |""".stripMargin
          case None =>
            ""
      else
        Option(colorProperty.get()) match
          case Some(color) =>
            s"""
               |-fx-border-width: 1pt;
               |-fx-border-radius: 5pt;
               |-fx-background-radius: 5pt;
               |-fx-border-color: ${ColorUtil.hexString(color.darker)};
               |-fx-background-color: transparent;
               |""".stripMargin
          case None =>
            ""

  def mkTextStyleBinding(colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(mkTextStyleCallable(colorProperty), colorProperty)

  def mkGradientStyleBinding(activeProperty: BooleanProperty, colorProperty: ObjectPropertyBase[Color]): StringBinding =
    Bindings.createStringBinding(gradientStyle(activeProperty, colorProperty), activeProperty, colorProperty)


  def mkContrastPropertyBinding(activeProperty: BooleanProperty, colorProperty: ObjectPropertyBase[Color]): ObjectBinding[Color] = Bindings.createObjectBinding(
    () =>
      (Option(colorProperty.get()), activeProperty.get()) match
        case (Some(value), true) => ColorUtil.getContrastColor(value)
        case _ => MutableSearchTerm.UnclassifiedColor.darker.darker
    , activeProperty, colorProperty
  )
