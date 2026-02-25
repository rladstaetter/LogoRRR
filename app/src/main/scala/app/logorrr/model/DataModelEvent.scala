package app.logorrr.model

import app.logorrr.conf.TimeSettings
import javafx.event.{Event, EventType}

object DataModelEvent:
  val DateFilterEvent: EventType[DateFilterEvent] = new EventType(Event.ANY, "ADD_DATE_FILTER")
  val RemoveDateFilterEvent: EventType[RemoveDateFilterEvent] = new EventType(Event.ANY, "REMOVE_DATE_FILTER")

case class DateFilterEvent(timeSettings: TimeSettings) extends Event(DataModelEvent.DateFilterEvent)

class RemoveDateFilterEvent extends Event(DataModelEvent.RemoveDateFilterEvent)
