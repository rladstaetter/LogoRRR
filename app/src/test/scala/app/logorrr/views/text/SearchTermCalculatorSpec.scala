package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.views.search.{MutableSearchTerm, SearchTerm}
import app.logorrr.{LogEntrySpec, LogoRRRSpec, TestUtil}
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color
import org.scalacheck.{Gen, Prop}

object SearchTermCalculatorSpec {

  val gen: Gen[LogTextViewLabel] =
    for {
      e <- LogEntrySpec.gen
      maxLength <- Gen.posNum[Int]
      searchTerms <- Gen.listOf(TestUtil.mutSearchTermGen)
    } yield LogTextViewLabel(e, maxLength, searchTerms, () => "", new SimpleIntegerProperty())
}

class SearchTermCalculatorSpec extends LogoRRRSpec {

  def applySingleFilter(logEntry: String, pattern: String): Seq[Seq[LinePart]] = {
    SearchTermCalculator(LogEntry(0, logEntry,  None, None), Seq(MutableSearchTerm(SearchTerm(pattern, Color.RED, active = true)))).filteredParts
  }

  "calcParts" should {
    "return empty List for empty search string" in {
      check(Prop.forAll(LogEntrySpec.gen) {
        le =>
          val filteredParts = applySingleFilter(le.value, "")
          filteredParts.length == 1 && filteredParts.head.isEmpty
      })
    }
    "return empty List for empty LogEntry string" in {
      check(Prop.forAll(TestUtil.mutSearchTermGen) {
        filter =>
          val filteredParts = applySingleFilter("", filter.getPredicate.description)
          filteredParts.length == 1 && filteredParts.head.isEmpty
      })
    }

    /** a single 'a' will match 4 times for log entry 'aaaa' */
    "return 4 parts" in assert(applySingleFilter("aaaa", "a").head.length == 4)
    "return 1 parts for 'aaa' with searchstring 'a'" in {
      val partss = applySingleFilter("aaa", "a")
      assert(partss.size == 1)

      val parts = partss.head
      assert(parts.head.startIndex == 0)
      assert(parts.head.endIndex == 0)
      assert(parts(1).startIndex == 1)
      assert(parts(1).endIndex == 1)
      assert(parts(2).startIndex == 2)
      assert(parts(2).endIndex == 2)

    }
    "return 2 parts for 'aaaa'" in {
      val partss = applySingleFilter("aaaa", "aa")
      assert(partss.size == 1)

      val parts = partss.head
      assert(parts.head.startIndex == 0)
      assert(parts.head.endIndex == 1)
      assert(parts(1).startIndex == 2)
      assert(parts(1).endIndex == 3)
    }

    "return 2 parts for 'aabaa'" in {
      val partss = applySingleFilter("aabaa", "aa")
      assert(partss.size == 1)
      val parts = partss.head
      assert(parts.size == 2)
      assert(parts.head.startIndex == 0)
      assert(parts.head.endIndex == 1)
      assert(parts(1).startIndex == 3)
      assert(parts(1).endIndex == 4)
    }

  }

  "filteredParts" should {
    val filters = Seq(
      MutableSearchTerm(SearchTerm("a", Color.RED, active = true))
      , MutableSearchTerm(SearchTerm("b", Color.BLUE, active = true))
      , MutableSearchTerm(SearchTerm("t", Color.YELLOW, active = true))
    )
    val entry = LogEntry(0, "test a b c",  None, None)
    val calculator = SearchTermCalculator(entry, filters)

    "produce correct amount of matches" in {
      val filteredParts = calculator.filteredParts
      assert(filteredParts.size == 3)
      assert(filteredParts.head.size == 1) // match a once
      assert(filteredParts(1).size == 1) // match b once
      assert(filteredParts(2).size == 2) // match t twice
    }

    "produce correct label content" in {
      val stringAndColor: Seq[(String, Color)] = calculator.stringColorPairs
      val jo = stringAndColor.map(_._1).foldLeft("")((acc, s) => s"$acc$s")
      assert(jo == entry.value)
    }
  }
}