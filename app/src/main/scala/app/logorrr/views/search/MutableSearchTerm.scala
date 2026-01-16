package app.logorrr.views.search

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.SearchTerm
import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.scene.paint.Color

import java.util.function.Predicate

object MutableSearchTerm:

  val UnclassifiedColor: Color = Color.LIGHTGREY
  val UnclassifiedText = "Unclassified"

  def apply(searchTerm: SearchTerm): MutableSearchTerm =
    apply(searchTerm.value, searchTerm.color, searchTerm.active)

  def mkUnclassified(otherPredicates: Set[Predicate[String]]): MutableSearchTerm = {
    val st = new MutableSearchTerm()
    st.setValue(UnclassifiedText)
    st.setColor(UnclassifiedColor)
    st.setActive(true)
    st.setPredicate(s => !otherPredicates.foldRight(false)((p, acc) => acc || p.test(s)))
    st.setUnclassified(true)
    st
  }

  def apply(searchTermAsString: String
            , color: Color
            , active: Boolean): MutableSearchTerm =
    val st = new MutableSearchTerm()
    st.setValue(searchTermAsString)
    st.setColor(color)
    st.setActive(active)
    st.setPredicate(s => Option(st.valueProperty.get()).exists(p => s.contains(p)))
    st.setUnclassified(false)
    st


class MutableSearchTerm extends BaseSearchTermModel
  with Predicate[String]:

  private val predicateProperty: SimpleObjectProperty[Predicate[String]] = new SimpleObjectProperty[Predicate[String]]()

  def setPredicate(p: Predicate[String]): Unit = predicateProperty.set(p)

  override def test(logLine: String): Boolean = predicateProperty.get().test(logLine)





