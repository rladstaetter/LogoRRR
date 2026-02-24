package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{LogoRRRGlobals, TimestampSettings}
import app.logorrr.model.LogEntry
import app.logorrr.views.search.TimestampSettingsRegion
import javafx.collections.ObservableList

import java.time.Instant
import java.util

object TimestampUtil {

  def calculate(mutLogFileSettings: MutLogFileSettings
                , chunkListView: ChunkListView[LogEntry]
                , logEntries: ObservableList[LogEntry]
                , timestampSettings: TimestampSettings
                , tsRegion: TimestampSettingsRegion): Unit = {
    mutLogFileSettings.setSomeTimestampSettings(Option(timestampSettings))
    LogoRRRGlobals.persist(LogoRRRGlobals.getSettings)

    chunkListView.removeInvalidationListener()
    var someFirstEntryTimestamp: Option[Instant] = None

    val tempList = new util.ArrayList[LogEntry]()
    for i <- 0 until logEntries.size() do {
      val e = logEntries.get(i)
      val someInstant = TimestampSettings.parseInstant(e.value, timestampSettings)
      if someFirstEntryTimestamp.isEmpty then {
        someFirstEntryTimestamp = someInstant
      }
      tempList.add(e.copy(someEpochMilli = someInstant.map(_.toEpochMilli)))
    }
    logEntries.setAll(tempList)
    chunkListView.addInvalidationListener()
    // update slider boundaries
    tsRegion.initializeRanges()

  }

}
