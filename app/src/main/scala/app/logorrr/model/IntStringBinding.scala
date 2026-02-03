package app.logorrr.model

import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleIntegerProperty

class IntStringBinding(dependency: SimpleIntegerProperty) extends StringBinding {
  bind(dependency)

  override def computeValue(): String = Option(dependency.get).map(_.toString).getOrElse("")
}
