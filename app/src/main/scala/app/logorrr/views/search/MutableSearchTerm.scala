package app.logorrr.views.search

import app.logorrr.clv.color.ColorMatcher
import app.logorrr.conf.{SearchTerm, Settings}
import app.logorrr.views.search.predicates.{ContainsPredicate, LabelledFunction}
import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleObjectProperty}
import javafx.scene.paint.Color

object MutableSearchTerm {

  val DefaultSearchTerms: Seq[MutableSearchTerm] = Settings.JavaLoggingGroup.terms.map(MutableSearchTerm.apply)

  def apply(searchTerm: SearchTerm): MutableSearchTerm = {
    apply(searchTerm.value, searchTerm.color, searchTerm.active)
  }

  private def apply(pattern: String
                    , color: Color
                    , active: Boolean): MutableSearchTerm = {
    apply(ContainsPredicate(pattern), color, active)
  }

  private def apply(predicate: LabelledFunction
                    , color: Color
                    , active: Boolean): MutableSearchTerm = {
    val filter = new MutableSearchTerm()
    filter.init(predicate, color, active)
    filter
  }

}


class MutableSearchTerm extends ColorMatcher {

  val predicateProperty: SimpleObjectProperty[LabelledFunction] = new SimpleObjectProperty[LabelledFunction]()
  val colorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  def init(predicate: LabelledFunction
           , color: Color
           , active: Boolean): Unit = {
    setPredicate(predicate)
    setColor(color)
    setActive(active)
  }

  def matches(searchTerm: String): Boolean = Option(predicateProperty.get()).exists(p => p.apply(searchTerm))

  def unbind(): Unit = activeProperty.unbind()

  def bind(activeProperty: BooleanProperty): Unit = this.activeProperty.bind(activeProperty)

  def getPredicate: LabelledFunction = predicateProperty.get()

  def setPredicate(predicate: LabelledFunction): Unit = predicateProperty.set(predicate)

  def getColor: Color = colorProperty.get()

  def setColor(color: Color): Unit = colorProperty.set(color)

  def setActive(active: Boolean): Unit = activeProperty.set(active)

  def isActive: Boolean = activeProperty.get()

}








