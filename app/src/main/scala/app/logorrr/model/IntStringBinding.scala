package app.logorrr.model

import javafx.beans.binding.StringBinding
import javafx.beans.property.{ObjectProperty, SimpleIntegerProperty}

class IntStringBinding(dependency: ObjectProperty[java.lang.Integer]) extends StringBinding {
  bind(dependency)

  override def computeValue(): String = Option(dependency.get).map(_.toString).getOrElse("")
}
