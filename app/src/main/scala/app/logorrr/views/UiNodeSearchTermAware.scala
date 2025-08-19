package app.logorrr.views

import app.logorrr.io.FileId

/**
 * UiNodes for filter toggle buttons need a reference to a file and its specific filter
 */
trait UiNodeSearchTermAware {

  def uiNode(fileId: FileId, searchTerm: MutableSearchTerm): UiNode

}
