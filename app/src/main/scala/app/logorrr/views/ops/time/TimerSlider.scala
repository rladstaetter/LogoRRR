package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.{UiNode, UiNodeFileIdAndPosAware}
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.{Slider, Tooltip}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}


object TimeUtil {


  /**
   * Given an observable list of log entries, calculate the min and max instant. LogEntries doesn't have to be ordered.
   *
   * @param logEntries list of log entries
   * @return min and max Instant of all log entries
   */
  def calcTimeInfo(logEntries: ObservableList[LogEntry]): Option[TimeRange] = {
    if (!logEntries.isEmpty) {
      var minInstant = Instant.MAX
      var maxInstant = Instant.MIN
      logEntries.forEach((e: LogEntry) => {
        e.someInstant match {
          case Some(instant) =>
            if (instant.isBefore(minInstant)) {
              minInstant = instant
            }
            if (instant.isAfter(maxInstant)) {
              maxInstant = instant
            }
          case None => // do nothing
        }
      })
      if (minInstant == Instant.MIN || minInstant == Instant.MAX) {
        None
      } else if (maxInstant == Instant.MIN || maxInstant == Instant.MAX) {
        None
      } else if (minInstant.equals(maxInstant)) {
        None
      } else {
        Option(TimeRange(minInstant, maxInstant))
      }
    } else None
  }
}

object TimerSlider extends UiNodeFileIdAndPosAware {

  val Width = 350

  def format(epochMilli: Long, formatter: DateTimeFormatter): String = {
    val instant = Instant.ofEpochMilli(epochMilli)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault)
    dateTime.format(formatter)
  }


  override def uiNode(id: FileId, pos: Pos): UiNode = UiNode(id, pos, classOf[TimerSlider])
}

class TimerSlider(mutLogFileSettings: MutLogFileSettings
                  , pos: Pos
                  , tooltipText: String
                  , range: TimeRange) extends Slider {

  setId(TimerSlider.uiNode(mutLogFileSettings.getFileId, pos).value)
  setPrefWidth(TimerSlider.Width)
  setTooltip(new Tooltip(tooltipText))
  setRange(range)
  visibleProperty().bind(mutLogFileSettings.hasTimestampSetting)
  disableProperty().bind(mutLogFileSettings.hasTimestampSetting.not)

  def setInstant(instant: Instant): Unit = setValue(instant.toEpochMilli.doubleValue())

  def setRange(range: TimeRange): Unit = {
    setMin(range.start.toEpochMilli.doubleValue)
    setMax(range.end.toEpochMilli.doubleValue())
  }
}