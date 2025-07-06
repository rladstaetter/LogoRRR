package app.logorrr.jfxbfr

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.scene.paint.Color


case class Ep(pattern: String) extends Function1[String, Boolean] {
  override def apply(searchTerm: String): Boolean = searchTerm.contains(pattern)
}

object Fltr {

  private val FinestFilter: Fltr[String] = Fltr(Ep("FINEST"), "FINEST", Color.GREY, active = true)
  private val InfoFilter: Fltr[String] = Fltr(Ep("INFO"), "INFO", Color.GREEN, active = true)
  private val WarningFilter: Fltr[String] = Fltr(Ep("WARNING"), "WARNING", Color.ORANGE, active = true)
  private val SevereFilter: Fltr[String] = Fltr(Ep("SEVERE"), "SEVERE", Color.RED, active = true)

  val DefaultFilters: Seq[Fltr[_]] = Seq(FinestFilter, InfoFilter, WarningFilter, SevereFilter)


  def apply[A](predicate: A => Boolean
               , pattern: String
               , color: Color
               , active: Boolean): Fltr[A] = {
    val fltr = new Fltr[A]()
    fltr.init(predicate, pattern, color, active)
    fltr
  }

}

class Fltr[A] {

  def init(predicate: A => Boolean
           , pattern: String, color: Color, active: Boolean): Unit = {
    setPredicate(predicate)
    setPattern(pattern)
    setColor(color)
    setActive(active)
  }

  val predicateProperty: SimpleObjectProperty[A => Boolean] = new SimpleObjectProperty[A => Boolean]()
  val patternProperty: SimpleStringProperty = new SimpleStringProperty()
  val colorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  def matches(searchTerm: String): Boolean = {
    Option(patternProperty.get()).exists(p => searchTerm.contains(p))
  }

  def getColor: Color = colorProperty.get()

  def getPattern: String = patternProperty.get()

  def isActive: Boolean = activeProperty.get()

  def unbind(): Unit = activeProperty.unbind()

  def bind(activeProperty: BooleanProperty): Unit = {
    this.activeProperty.bind(activeProperty)
  }

  def setPredicate(predicate: A => Boolean): Unit = predicateProperty.set(predicate)

  def setPattern(pattern: String): Unit = patternProperty.set(pattern)

  def setColor(color: Color): Unit = colorProperty.set(color)

  def setActive(active: Boolean): Unit = activeProperty.set(active)

}








