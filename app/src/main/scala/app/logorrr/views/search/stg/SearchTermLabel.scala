package app.logorrr.views.search.stg

import app.logorrr.conf.SearchTerm
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.util.CssBindingUtil
import javafx.scene.control.Label


object SearchTermLabel:

  def apply(searchTerm : SearchTerm) : SearchTermLabel =
    apply(MutableSearchTerm(searchTerm))

  def apply(mutSearchTerm: MutableSearchTerm): SearchTermLabel =
    val b = new SearchTermLabel()
    b.textProperty.bind(mutSearchTerm.valueProperty)
    b.styleProperty().bind(CssBindingUtil.mkTextStyleBinding(mutSearchTerm.colorProperty))
    b

class SearchTermLabel extends Label
