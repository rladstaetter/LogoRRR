package app.logorrr.usecases.stg

import app.logorrr.TestFiles
import app.logorrr.conf.Settings
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.stg.StgChoiceBox
import org.junit.jupiter.api.Test

class StgCheckDefaultTest extends SingleFileApplicationTest(TestFiles.simpleLog0) with StgEditorActions:

  @Test def checkChoiceBoxEmptyOnStart(): Unit =
    // open file such that search term group editor icon appears
    openFile(fileId)

    // wait for visibility
    waitForVisibility(StgChoiceBox.uiNode(fileId))

    matchItems[String](StgChoiceBox.uiNode(fileId), Settings.DefaultSearchTermGroups.map(_.name).sorted)
