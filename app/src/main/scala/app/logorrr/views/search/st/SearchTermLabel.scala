package app.logorrr.views.search.st

import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.StringBinding
import javafx.beans.property.{ObjectPropertyBase, StringProperty}
import javafx.scene.control.Label
import javafx.scene.paint.Color

class SearchTermLabel extends Label:

  def init(contrastColorProperty: ObjectPropertyBase[Color], valueProperty: StringProperty): Unit =
    styleProperty().bind(CssBindingUtil.mkTextStyleBinding(contrastColorProperty))
    textProperty().bind(valueProperty)

  def shutdown(): Unit =
    textProperty.unbind()
    styleProperty.unbind()
