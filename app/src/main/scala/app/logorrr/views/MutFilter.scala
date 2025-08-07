package app.logorrr.views

import app.logorrr.clv.color.ColorMatcher
import app.logorrr.views.search.predicates.DescriptivePredicate
import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleObjectProperty}
import javafx.scene.paint.Color

object MutFilter {

  def apply[A](predicate: DescriptivePredicate
               , color: Color
               , active: Boolean): MutFilter[A] = {
    val filter = new MutFilter[A]()
    filter.init(predicate, color, active)
    filter
  }

}



class MutFilter[A] extends ColorMatcher {

  val predicateProperty: SimpleObjectProperty[DescriptivePredicate] = new SimpleObjectProperty[DescriptivePredicate]()
  val colorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  def init(predicate: DescriptivePredicate
           , color: Color
           , active: Boolean): Unit = {
    setPredicate(predicate)
    setColor(color)
    setActive(active)
  }

  def matches(searchTerm: String): Boolean = Option(predicateProperty.get()).exists(p => p.apply(searchTerm))

  def unbind(): Unit = activeProperty.unbind()

  def bind(activeProperty: BooleanProperty): Unit = this.activeProperty.bind(activeProperty)

  def getPredicate: DescriptivePredicate = predicateProperty.get()
  def setPredicate(predicate: DescriptivePredicate): Unit = predicateProperty.set(predicate)

  def getColor: Color = colorProperty.get()
  def setColor(color: Color): Unit = colorProperty.set(color)

  def setActive(active: Boolean): Unit = activeProperty.set(active)
  def isActive: Boolean = activeProperty.get()

}








