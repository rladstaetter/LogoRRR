package app.logorrr.views.search.st

import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.{Bindings, StringBinding}
import javafx.beans.property.{IntegerProperty, ObjectProperty, ObjectPropertyBase}
import javafx.scene.control.Label
import javafx.scene.paint.Color

class SearchTermHitsLabel extends Label:

  def init(colorProperty: ObjectPropertyBase[Color], hitsProperty: IntegerProperty): Unit =
    styleProperty().bind(CssBindingUtil.mkTextStyleBinding(colorProperty))
    textProperty().bind(Bindings.createStringBinding(() => s"Count: ${hitsProperty.get()}", hitsProperty))

  def shutdown(): Unit =
    textProperty.unbind()
    styleProperty.unbind()
