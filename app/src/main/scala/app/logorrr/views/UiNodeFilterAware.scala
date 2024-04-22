package app.logorrr.views

import app.logorrr.views.search.Filter

trait UiNodeFilterAware {
  def uiNode(filter: Filter): UiNode
}
