package app.logorrr.steps

import app.logorrr.io.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.text.LogTextView

trait LogTextViewActions {
  self: TestFxBaseApplicationTest =>

  def lookupLogTextView(fileId: FileId): LogTextView = {
    val logTextViewUiElem = LogTextView.uiNode(fileId)
    lookup(logTextViewUiElem.ref).query[LogTextView]
  }

}


