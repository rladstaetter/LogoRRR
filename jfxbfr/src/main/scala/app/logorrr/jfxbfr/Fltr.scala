package app.logorrr.jfxbfr

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.scene.paint.Color

object Fltr {

  private val FinestFilter: Fltr = Fltr("FINEST", Color.GREY, active = true)
  private val InfoFilter: Fltr = Fltr("INFO", Color.GREEN, active = true)
  private val WarningFilter: Fltr = Fltr("WARNING", Color.ORANGE, active = true)
  private val SevereFilter: Fltr = Fltr("SEVERE", Color.RED, active = true)

  val DefaultFilters: Seq[Fltr] = Seq(FinestFilter, InfoFilter, WarningFilter, SevereFilter)


  def apply(pattern: String
            , color: Color
            , active: Boolean): Fltr = {
    val fltr = new Fltr()
    fltr.init(pattern, color, active)
    fltr
  }

}

class Fltr {

  def init(pattern: String, color: Color, active: Boolean): Unit = {
    setPattern(pattern)
    setColor(color)
    setActive(active)
  }

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

  def setPattern(pattern: String): Unit = patternProperty.set(pattern)

  def setColor(color: Color): Unit = colorProperty.set(color)

  def setActive(active: Boolean): Unit = activeProperty.set(active)

}








