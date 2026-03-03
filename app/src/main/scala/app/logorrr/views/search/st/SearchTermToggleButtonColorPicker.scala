package app.logorrr.views.search.st

import app.logorrr.model.{DataModelEvent, UpdateLogFilePredicate}
import app.logorrr.views.util.GfxElements
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color
import javafx.util.Subscription

import scala.compiletime.uninitialized

class SearchTermToggleButtonColorPicker extends ColorPicker:
  setTooltip(GfxElements.ToolTips.mkColorPicker)
  getStyleClass.add(ColorPicker.STYLE_CLASS_BUTTON)
  setStyle("""
             |-fx-color-label-visible: false;
             |-fx-padding: 0pt;
             |-fx-spacing: 0pt;
             |-fx-border-width: 0pt;
             |-fx-border-radius: 0pt;
             |-fx-background-radius: 0pt;
             |-fx-border-color: transparent;
             |-fx-background-color: transparent;""".stripMargin)

  var valueSubscription : Subscription = uninitialized

  def init(colorProperty: ObjectPropertyBase[Color]): Unit = {
    valueProperty().bindBidirectional(colorProperty)
    valueSubscription = valueProperty().subscribe(v => fireEvent(UpdateLogFilePredicate()))
  }

  def shutdown(colorProperty: ObjectPropertyBase[Color]): Unit = {
    valueProperty().unbindBidirectional(colorProperty)
    valueSubscription.unsubscribe()
  }
