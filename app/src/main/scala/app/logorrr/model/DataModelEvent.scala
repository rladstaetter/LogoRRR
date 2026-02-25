package app.logorrr.model

import app.logorrr.conf.TimeSettings
import app.logorrr.views.search.MutableSearchTerm
import javafx.event.{Event, EventType}

object DataModelEvent:
  val DateFilterEvent: EventType[DateFilterEvent] = new EventType(Event.ANY, "ADD_DATE_FILTER")
  val RemoveDateFilterEvent: EventType[RemoveDateFilterEvent] = new EventType(Event.ANY, "REMOVE_DATE_FILTER")
  val AddSearchTermButtonEvent: EventType[AddSearchTermButtonEvent] = new EventType(Event.ANY, "ADD_SEARCHTERM_BUTTON")
  val RemoveSearchTermButtonEvent: EventType[RemoveSearchTermButtonEvent] = new EventType(Event.ANY, "REMOVE_SEARCHTERM_BUTTON")
  val UpdateLogFilePredicate: EventType[UpdateLogFilePredicate] = new EventType(Event.ANY, "UPDATE_LOGFILE_PREDICATE")

case class DateFilterEvent(timeSettings: TimeSettings) extends Event(DataModelEvent.DateFilterEvent)

case class AddSearchTermButtonEvent(mutableSearchTerm: MutableSearchTerm) extends Event(DataModelEvent.AddSearchTermButtonEvent)

case class RemoveSearchTermButtonEvent(mutableSearchTerm: MutableSearchTerm) extends Event(DataModelEvent.RemoveSearchTermButtonEvent)

class RemoveDateFilterEvent extends Event(DataModelEvent.RemoveDateFilterEvent)

class UpdateLogFilePredicate extends Event(DataModelEvent.UpdateLogFilePredicate)