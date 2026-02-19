package app.logorrr.views.search

import app.logorrr.conf.SearchTerm
import app.logorrr.model.{ActivePropertyHolder, ColorPropertyHolder, UnclassifiedPropertyHolder, ValuePropertyHolder}
import javafx.beans.property.*


/**
 * Groups important attributes for search terms together
 */
trait BaseSearchTermModel
  extends ValuePropertyHolder
    with ColorPropertyHolder
    with ActivePropertyHolder
    with UnclassifiedPropertyHolder:

  def bindSearchTerm(other: MutableSearchTerm): Unit = {
    bindValueProperty(other.valueProperty)
    bindColorProperty(other.colorProperty)
    bindBidirectionalActiveProperty(other.activeProperty)
    bindUnclassifiedProperty(other.unclassifiedProperty)
  }

  def unbindSearchTerm(activeProperty: BooleanProperty): Unit = {
    unbindValueProperty()
    unbindColorProperty()
    unbindBidirectionalActiveProperty(activeProperty)
    unbindUnclassfiedProperty()
  }

  def asSearchTerm: SearchTerm = SearchTerm(getValue, getColor, isActive)
