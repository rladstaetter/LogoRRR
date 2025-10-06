package app.logorrr.views.a11y

import app.logorrr.io.FileId
import app.logorrr.views.search.MutableSearchTerm

/**
 * UiNodes for filter toggle buttons need a reference to a file and its specific filter
 */
trait UiNodeSearchTermAware {

  def uiNode(fileId: FileId, searchTerm: MutableSearchTerm): UiNode

}
