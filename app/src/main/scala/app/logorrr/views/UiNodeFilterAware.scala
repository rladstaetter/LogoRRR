package app.logorrr.views

import app.logorrr.io.FileId
import app.logorrr.jfxbfr.MutFilter

/**
 * UiNodes for filter toggle buttons need a reference to a file and its specific filter
 */
trait UiNodeFilterAware {

  def uiNode(fileId: FileId, filter: MutFilter[_]): UiNode

}
