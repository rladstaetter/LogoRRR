package app.logorrr.views.ops.time

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.block.ChunkListView
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.{Label, Slider, ToolBar}

import java.time.format.DateTimeFormatter

object TimeOpsToolBar {
  private def updateFilteredList(filteredList: FilteredList[LogEntry], lower: Long, higher: Long): Unit = {
    filteredList.setPredicate((t: LogEntry) => {
      t.someInstant match {
        case Some(value) =>
          val asMilli = value.toEpochMilli
          lower <= asMilli && asMilli <= higher
        case None => false
      }
    })
  }
}

class TimestampSliderLabel(hasLogEntrySettingBinding: BooleanBinding, slider: Slider, formatter: DateTimeFormatter) extends Label {
  textProperty().bind(Bindings.createStringBinding(() => TimerSlider.format(slider.getValue.longValue(), formatter), slider.valueProperty))
  visibleProperty().bind(hasLogEntrySettingBinding)
}

class TimeOpsToolBar(mutLogFileSettings: MutLogFileSettings
                     , chunkListView: ChunkListView
                     , logEntries: ObservableList[LogEntry]
                     , filteredList: FilteredList[LogEntry]) extends ToolBar {

  val fileId: FileId = mutLogFileSettings.getFileId
  val formatter: DateTimeFormatter = mutLogFileSettings.someLogEntrySettingsProperty.get().map(_.dateTimeFormatter).getOrElse(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))

  private val lowSlider = new TimerSlider(mutLogFileSettings.hasLogEntrySettingBinding, "Configure earliest timestamp to be displayed")
  private val highSlider = new TimerSlider(mutLogFileSettings.hasLogEntrySettingBinding, "Configure latest timestamp to be displayed")
  private val leftLabel = new TimestampSliderLabel(mutLogFileSettings.hasLogEntrySettingBinding, lowSlider, formatter)
  private val rightLabel = new TimestampSliderLabel(mutLogFileSettings.hasLogEntrySettingBinding, highSlider, formatter)

/*
  var changeCnt = 0
  val lcl = JfxUtils.mkListChangeListener {
    c: ListChangeListener.Change[_ <: LogEntry] => {
      while (c.next()) {
        updateSliderBoundaries()
        println(s"change $changeCnt")
        changeCnt = changeCnt + 1
      }
    }
  }
*/
  // filteredList.addListener(lcl)

  /**
   * To configure the logformat of the timestamp used in a logfile
   */
  private val timestampSettingsButton = new TimestampSettingsButton(LogoRRRGlobals.getLogFileSettings(fileId), chunkListView, logEntries)

  lowSlider.setValue(lowSlider.getMin)
  highSlider.setValue(highSlider.getMax)

  lowSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue > highSlider.getValue) lowSlider.setValue(highSlider.getValue)
    TimeOpsToolBar.updateFilteredList(filteredList, newValue.longValue(), highSlider.getValue.longValue)
  })

  highSlider.valueProperty.addListener((_, _, newValue) => {
    if (newValue.doubleValue < lowSlider.getValue) highSlider.setValue(lowSlider.getValue)
    TimeOpsToolBar.updateFilteredList(filteredList, lowSlider.getValue.longValue, newValue.longValue())
  })

  getItems.addAll(Seq(timestampSettingsButton, leftLabel, lowSlider, highSlider, rightLabel): _*)

  updateSliderBoundaries()

  private def updateSliderBoundaries(): Unit = {
    TimerSlider.calculateBoundaries(filteredList) match {
      case Some((minInstant, maxInstant)) =>
        lowSlider.setMin(minInstant.toEpochMilli.doubleValue())
        highSlider.setMin(minInstant.toEpochMilli.doubleValue())
        lowSlider.setMax(maxInstant.toEpochMilli.doubleValue())
        highSlider.setMax(maxInstant.toEpochMilli.doubleValue())
        lowSlider.setValue(lowSlider.getMin)
        highSlider.setValue(highSlider.getMax)
        println("set" + minInstant + "/" + maxInstant)
      case None =>
        println("none")
    }
  }

}



