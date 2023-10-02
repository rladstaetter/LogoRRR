package app.logorrr.views.text

import app.logorrr.conf.mut.FilterSpec
import app.logorrr.model.LogEntry
import app.logorrr.views.search.Filter
import app.logorrr.{LogEntrySpec, LogoRRRSpec}
import javafx.scene.paint.Color
import org.scalacheck.{Gen, Prop}

object FilterCalculatorSpec {

  val gen: Gen[LogTextViewLabel] =
    for {
      e <- LogEntrySpec.gen
      maxLength <- Gen.posNum[Int]
      filters <- Gen.listOf(FilterSpec.gen)
    } yield LogTextViewLabel(e, maxLength, filters, () => "")
}

class FilterCalculatorSpec extends LogoRRRSpec {

  def applySingleFilter(logEntry: String, pattern: String): Seq[Seq[LinePart]] = {
    FilterCalculator(LogEntry(0,logEntry, None), Seq(new Filter(pattern, Color.RED, true))).filteredParts
  }

  "calcParts" should {
    "return empty List for empty search string" in {
      check(Prop.forAll(LogEntrySpec.gen) {
        le =>
          val filteredParts = applySingleFilter(le.value, "")
          filteredParts.length == 1 && filteredParts.head.isEmpty
      })
    }
    "return empty List for empty logentry string" in {
      check(Prop.forAll(FilterSpec.gen) {
        filter =>
          val filteredParts = applySingleFilter("", filter.pattern)
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
      new Filter("a", Color.RED, true)
      , new Filter("b", Color.BLUE, true)
      , new Filter("t", Color.YELLOW, true)
    )
    val entry = LogEntry(0, "test a b c", None)
    val calculator = FilterCalculator(entry, filters)

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