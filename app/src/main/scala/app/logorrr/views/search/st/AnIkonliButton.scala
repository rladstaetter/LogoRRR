package app.logorrr.views.search.st

import app.logorrr.clv.color.ColorUtil
import javafx.beans.binding.BooleanBinding
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.paint.Color
import org.kordamp.ikonli.javafx.FontIcon

object AnIkonliButton:

  val cssStyle: String =
    """
      |-fx-padding: 0pt;
      |-fx-spacing: 0pt;
      |-fx-border-width: 0pt;
      |-fx-border-radius: 0pt;
      |-fx-background-radius: 0pt;
      |-fx-border-color: transparent;
      |-fx-background-color: transparent;""".stripMargin

  /** css gymnastics to make remove button look pretty */
  def buttonCssStyle(icon: FontIcon, color: Color): String =
    s"""
       |-fx-padding: 0;
       |-fx-border-width: 0pt;
       |-fx-border-radius: 0pt;
       |-fx-background-radius: 0pt;
       |-fx-font-family: 'Font Awesome 6 Free Regular';
       |-fx-icon-size: 16;
       |-fx-icon-code: ${icon.getIconLiteral};
       |-fx-icon-color: ${ColorUtil.hexString(color)};
       |""".stripMargin

class AnIkonliButton(val icon: FontIcon, tooltip: Tooltip) extends Button:
  setGraphic(icon)
  setTooltip(tooltip)
  setStyle(AnIkonliButton.cssStyle)

  def init(visibleBinding: BooleanBinding): Unit =
    visibleProperty().bind(visibleBinding)

  def shutdown(): Unit =
    icon.iconColorProperty().unbind()