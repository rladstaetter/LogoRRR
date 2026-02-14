package app.logorrr.views.search

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.model.{ActivePropertyHolder, ColorPropertyHolder, UnclassifiedPropertyHolder, ValuePropertyHolder}
import javafx.beans.property.*
import javafx.scene.paint.Color


/**
 * Groups important attributes for search terms together
 */
trait BaseSearchTermModel
  extends ValuePropertyHolder
    with ColorPropertyHolder
    with ActivePropertyHolder
    with UnclassifiedPropertyHolder:

  def bindSearchTerm(mutableSearchTerm: MutableSearchTerm): Unit = {
    bindValueProperty(mutableSearchTerm.valueProperty)
    bindColorProperty(mutableSearchTerm.colorProperty)
    bindActiveProperty(mutableSearchTerm.activeProperty)
  }

  def unbindSearchTerm(): Unit = {
    unbindValueProperty()
    unbindColorProperty()
    unbindActiveProperty()
  }

  def asSearchTerm: SearchTerm = SearchTerm(getValue, getColor, isActive)
