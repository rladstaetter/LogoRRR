package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.conf.TestSettings
import app.logorrr.steps.ChoiceBoxActions
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.{RemoveSearchTermButton, ASearchTermToggleButton}
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
  with ChoiceBoxActions:

  // use default search terms
  val terms: Seq[MutableSearchTerm] = TestSettings.DefaultSearchTerms

  // each filter has one line + 1 line which is unclassified
  val lineCount: Int = terms.size + 1

  @Test def selectSpecificSearchTerm(): Unit =
    // file has 8 entries - one for each log filter + 1 line "unclassified"
    // some log lines are matched by more than one filter (FINE, FINER)
    openFile(fileId)

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

        // now, only 'unclassified' filter is active. since there are no unclassified
        // entries available, the number of displayed log entries is one
        checkNumberOfShownElements(1)

        // select one specific filter- now two lines are shown in total
        clickOn(ASearchTermToggleButton.uiNode(fileId, t.getValue))
        numberOfShownElementsIsAtLeast(2) // because of FINE, FINER and FINEST (FINE matches all three)

        // deselect filter again
        clickOn(ASearchTermToggleButton.uiNode(fileId, t.getValue))
        // clickFilters(terms)


    // finally, deselect all filters
    clickFilters(terms)

    // one entry is shown (unclassified)
    checkNumberOfShownElements(lineCount)

    // deselect unclassified search term
    clickFilters(Seq(MutableSearchTerm.mkUnclassified(terms.toSet)))
    checkNumberOfShownElements(lineCount - 1)

    terms.foreach(t => waitAndClickVisibleItem(RemoveSearchTermButton.uiNode(fileId, t.getValue)))

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

