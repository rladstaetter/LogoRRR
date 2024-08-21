package app.logorrr.views.ops.time

import app.logorrr.model.LogEntry
import javafx.collections.FXCollections
import org.scalatest.wordspec.AnyWordSpec

import java.time.{Duration, Instant}
import java.util.Collections

class TimerSliderSpec extends AnyWordSpec {

  "TimerSlider.calculateBoundaries" should {
    ".empty" in {
      val l = FXCollections.observableList(Collections.emptyList[LogEntry]())
      assert(TimerSlider.calcTimeInfo(l).isEmpty)
    }
    ".one entry without timestamp" in {
      val l = FXCollections.observableList(Collections.singletonList[LogEntry](LogEntry(0, "", None, None)))
      assert(TimerSlider.calcTimeInfo(l).isEmpty)
    }
    ".more entries without timestamp" in {
      val list = List.fill(100)(LogEntry(0, "", None, None))
      val l = FXCollections.observableArrayList(list: _*)
      assert(TimerSlider.calcTimeInfo(l).isEmpty)
    }
    ".one entry with timestamp" in {
      val l = FXCollections.observableList(Collections.singletonList[LogEntry](LogEntry(0, "", Option(Instant.now), Option(Duration.ofSeconds(0)))))
      assert(TimerSlider.calcTimeInfo(l).isDefined)
    }
    ".two entries with timestamp" in {
      val l = FXCollections.observableArrayList(
        LogEntry(0, "", Option(Instant.now), Option(Duration.ofSeconds(0))), LogEntry(1, "", Option(Instant.now.plusSeconds(10)), Option(Duration.ofSeconds(10))))
      assert(TimerSlider.calcTimeInfo(l).isDefined)
    }

  }
}
