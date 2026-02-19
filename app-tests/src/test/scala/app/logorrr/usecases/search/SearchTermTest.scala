package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.conf.TestSettings
import app.logorrr.steps.{ChoiceBoxActions, SearchTermToolbarActions}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.{ASearchTermToggleButton, RemoveSearchTermButton}
import app.logorrr.views.text.LogTextView
import org.junit.jupiter.api.Test

/**
 * Shows that enabling/disabling search terms has an impact on the textview.
 *
 * The selected log file (its log lines) corresponds to the used filters; each filter
 * hits at least one log line (some - FINE,FINER,FINEST) hit more than once which is perfectly ok.
 *
 **/
class SearchTermTest extends SingleFileApplicationTest(TestFiles.simpleLog2)
  with ChoiceBoxActions
  with SearchTermToolbarActions:

  // use default search terms
  val terms: Seq[MutableSearchTerm] = TestSettings.DefaultSearchTerms

  // each filter has one line + 1 line which is unclassified
  val lineCount: Int = terms.size + 1

  @Test def selectSpecificSearchTerm(): Unit =
    // file has 8 entries - one for each log filter + 1 line "unclassified"
    // some log lines are matched by more than one filter (FINE, FINER)
    openFile(fileId)

    clickFilters(Seq(MutableSearchTerm.mkUnclassified(terms.toSet)))

    // check visibility of all search term buttons
    terms.foreach:
      f => waitForVisibility(ASearchTermToggleButton.uiNode(fileId, f.getValue))

    // deselect all search terms
    clickFilters(terms)

    // this loop traverses all search terms and activates one by one
    // which yields at least one search result (for 'FINE' it yields three)
    terms.foreach:
      t =>
        // deselect all filters except unclassified
        // clickFilters(terms)

        // all elements are deselected
        checkNumberOfShownElements(0)

        // select one filter
        clickOn(ASearchTermToggleButton.uiNode(fileId, t.getValue))
        numberOfShownElementsIsAtLeast(1) // because of FINE, FINER and FINEST (FINE matches all three)

        // deselect filter again
        clickOn(ASearchTermToggleButton.uiNode(fileId, t.getValue))


    // finally, select all filters
    clickFilters(terms)

    checkNumberOfShownElements(lineCount -1 )

    // select unclassified search term
    clickFilters(Seq(MutableSearchTerm.mkUnclassified(terms.toSet)))
    checkNumberOfShownElements(lineCount)

    clearAllSearchTerms(fileId)
    // remove all filers
    // terms.foreach(t => waitAndClickVisibleItem(RemoveSearchTermButton.uiNode(fileId, t.getValue)))

    // all filters are deleted, unclassified filter is selected -> all log file lines are shown
    checkNumberOfShownElements(lineCount)


  private def lookupItemSize: Int = lookup(LogTextView.uiNode(fileId).ref).query[LogTextView].getItems.size()


  private def checkNumberOfShownElements(expectedElements: Int): Unit =
    val iSize: Int = lookupItemSize
    assert(iSize == expectedElements, s"Expected $expectedElements but was $iSize")

  private def numberOfShownElementsIsAtLeast(expectedElements: Int): Unit =
    val iSize: Int = lookupItemSize
    assert(iSize >= expectedElements, s"Expected at least $expectedElements but was $iSize")


  private def clickFilters(filters: Seq[MutableSearchTerm]): Unit =
    filters.foreach:
      ff => clickOn(ASearchTermToggleButton.uiNode(fileId, ff.getValue)) // enable all filters

