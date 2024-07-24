package app.logorrr.issues

import app.logorrr.TestFiles
import app.logorrr.model.LogFileSettings
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.search.FilterButton
import javafx.scene.control.ToggleButton
import org.junit.jupiter.api.Test
import org.testfx.api.FxAssert

import java.util.function.Predicate

/**
 * https://github.com/rladstaetter/LogoRRR/issues/236
 *
 * Shows that the default filters are used for a file after the first file was opened and the filter selection
 * was changed.
 * */
class Issue236OpenMultipleTabsAndChooseDefaultFilterTest
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions {

   @Test def testIssue236(): Unit = {
    // open first file
    openFile(TestFiles.simpleLog0)

    // change filters to a non default configuration
    val firstFilterTab1 = FilterButton.uiNode(TestFiles.simpleLog0, LogFileSettings.DefaultFilters.head)
    waitAndClickVisibleItem(firstFilterTab1)

    // check that the toggle button is deselected
    FxAssert.verifyThat(lookup(firstFilterTab1.ref), new Predicate[ToggleButton] {
      override def test(t: ToggleButton): Boolean = !t.isSelected
    })

    // open second file
    openFile(TestFiles.simpleLog1)

    // test that second file has the default filter configuration
    val firstFilterTab2 = FilterButton.uiNode(TestFiles.simpleLog1, LogFileSettings.DefaultFilters.head)
    FxAssert.verifyThat(lookup(firstFilterTab2.ref), new Predicate[ToggleButton] {
      override def test(t: ToggleButton): Boolean = t.isSelected
    })
  }

}
