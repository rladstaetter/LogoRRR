package app.logorrr.views.search.stg

import app.logorrr.conf.SearchTerm
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.util.CssBindingUtil
import javafx.geometry.Insets
import javafx.scene.control.Label


object SearchTermLabel:

  def apply(searchTerm: SearchTerm): SearchTermLabel =
    apply(MutableSearchTerm(searchTerm))

  def apply(mutSearchTerm: MutableSearchTerm): SearchTermLabel =
    val b = new SearchTermLabel()
    b.textProperty.bind(mutSearchTerm.valueProperty)
    b.setPadding(new Insets(10, 10, 10, 10))
    if (mutSearchTerm.isActive)
      b.styleProperty().bind(CssBindingUtil.mkGradientStyleBinding(mutSearchTerm.activeProperty, mutSearchTerm.colorProperty))
      b.textFillProperty().bind(CssBindingUtil.mkContrastPropertyBinding(mutSearchTerm.activeProperty, mutSearchTerm.colorProperty))
    else
      b.textFillProperty().bind(mutSearchTerm.colorProperty)
    b

class SearchTermLabel extends Label
