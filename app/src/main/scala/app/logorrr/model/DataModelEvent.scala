package app.logorrr.model

import app.logorrr.conf.TimestampSettings
import app.logorrr.views.logfiletab.TabControlEvent
import javafx.event.{Event, EventType}

object DataModelEvent:
  val AddTimeRangeFilterEvent: EventType[DateFilterEvent] = new EventType(Event.ANY, "ADD_TIMERANGE_FILTER")

case class DateFilterEvent(timestampSettings: TimestampSettings) extends Event(DataModelEvent.AddTimeRangeFilterEvent)