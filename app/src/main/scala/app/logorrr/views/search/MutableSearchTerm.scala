package app.logorrr.views.search

import app.logorrr.conf.SearchTerm
import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.scene.paint.Color

import java.util.function.Predicate

object MutableSearchTerm:

  def apply(searchTerm: SearchTerm): MutableSearchTerm =
    apply(searchTerm.value, searchTerm.color, searchTerm.active)

  def apply(searchTermAsString: String
            , color: Color
            , active: Boolean): MutableSearchTerm =
    val filter = new MutableSearchTerm()
    filter.setSearchTermAsString(searchTermAsString)
    filter.setColor(color)
    filter.setActive(active)
    filter


class MutableSearchTerm extends Predicate[String]:

  val searchTermAsStringProperty: SimpleStringProperty = new SimpleStringProperty()
  val colorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  override def test(logLine: String): Boolean = Option(searchTermAsStringProperty.get()).exists(p => logLine.contains(p))

  def unbind(): Unit = activeProperty.unbind()

  def bind(activeProperty: BooleanProperty): Unit = this.activeProperty.bind(activeProperty)

  def getSearchTermAsString: String = searchTermAsStringProperty.get()

  def setSearchTermAsString(searchTerm: String): Unit = searchTermAsStringProperty.set(searchTerm)

  def getColor: Color = colorProperty.get()

  def setColor(color: Color): Unit = colorProperty.set(color)

  def setActive(active: Boolean): Unit = activeProperty.set(active)

  def isActive: Boolean = activeProperty.get()

