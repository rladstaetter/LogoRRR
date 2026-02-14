package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.conf.{FileId, TestSettings}
import app.logorrr.steps.{CheckTabPaneActions, ChoiceBoxActions}
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.a11y.UiNode
import app.logorrr.views.search.st.SearchTermToggleButton
import app.logorrr.views.search.stg.StgChoiceBox
import javafx.scene.control.ToggleButton
import org.junit.jupiter.api.{Disabled, Test}
import org.testfx.api.FxAssert

import java.util.function.Predicate

/**
 * https://github.com/rladstaetter/LogoRRR/issues/236
 *
 * Shows that the default search terms are used for a file after the first file was opened and the it's search term selection
 * was changed.
 *
 * */
class Issue236OpenMultipleTabsAndChooseDefaultSearchTermTest
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions with ChoiceBoxActions:

  def activate(fileId: FileId, searchTermGroupName: String): Unit =
    openFile(fileId)
    matchItems[String](StgChoiceBox.uiNode(fileId), settings.searchTermGroups.keySet.toSeq.sorted)
    selectChoiceBoxByValue(StgChoiceBox.uiNode(fileId))(searchTermGroupName)

  @Disabled
  @Test def testIssue236(): Unit =
    // open first file
    val firstFile = TestFiles.simpleLog0
    activate(firstFile, TestSettings.Java_JUL)

    // change filters to a non default configuration
    val firstFilterTab1 = SearchTermToggleButton.uiNode(firstFile, TestSettings.DefaultSearchTerms.head.getValue)
    waitAndClickVisibleItem(firstFilterTab1)

    // check that the toggle button is deselected
    FxAssert.verifyThat(lookup(firstFilterTab1.ref), new Predicate[ToggleButton] {
      override def test(t: ToggleButton): Boolean = !t.isSelected
    })

    // open second file
    val secondFile = TestFiles.simpleLog1
    activate(secondFile, TestSettings.Java_JUL)

    // test that second file has the default filter configuration
    val firstFilterTab2: UiNode = SearchTermToggleButton.uiNode(secondFile, TestSettings.DefaultSearchTerms.head.getValue)
    waitForVisibility(firstFilterTab2)

    FxAssert.verifyThat(lookup(firstFilterTab2.ref), new Predicate[ToggleButton] {
      override def test(t: ToggleButton): Boolean = t.isSelected
    })

