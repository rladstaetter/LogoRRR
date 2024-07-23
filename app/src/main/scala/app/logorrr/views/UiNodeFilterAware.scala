package app.logorrr.views

import app.logorrr.io.FileId
import app.logorrr.views.search.Filter

/**
 * UiNodes for filter toggle buttons need a reference to a file and its specific filter
 */
trait UiNodeFilterAware {

  def uiNode(fileId: FileId, filter: Filter): UiNode

}
