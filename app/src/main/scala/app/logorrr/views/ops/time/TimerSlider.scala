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
  def calcTimeInfo(logEntries: ObservableList[LogEntry]): Option[TimeInfo] = {
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
      } else Option(TimeInfo(minInstant, maxInstant))
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
                  , tooltipText: String) extends Slider {

  setId(TimerSlider.uiNode(mutLogFileSettings.getFileId, pos).value)

  visibleProperty().bind(mutLogFileSettings.hasTimestampSetting)
  disableProperty().bind(mutLogFileSettings.hasTimestampSetting.not)
  setTooltip(new Tooltip(tooltipText))
  setPrefWidth(TimerSlider.Width)

  def setBoundsAndValue(minInstant: Instant
                        , maxInstant: Instant
                        , value: Double): Unit = {
    setMin(minInstant.toEpochMilli.doubleValue())
    setMax(maxInstant.toEpochMilli.doubleValue())
    setValue(value)
  }
}