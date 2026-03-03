package app.logorrr.model

import app.logorrr.conf.{SearchTerm, TimeSettings}
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.SearchTermToggleButton
import javafx.event.{Event, EventType}

object DataModelEvent:
  val DateFilterEvent: EventType[DateFilterEvent] = new EventType(Event.ANY, "ADD_DATE_FILTER")
  val RemoveDateFilterEvent: EventType[RemoveDateFilterEvent] = new EventType(Event.ANY, "REMOVE_DATE_FILTER")
  val AddSearchTermButtonEvent: EventType[AddSearchTermButtonEvent] = new EventType(Event.ANY, "ADD_SEARCHTERM_BUTTON")
  val RemoveSearchTermButtonEvent: EventType[RemoveSearchTermButtonEvent] = new EventType(Event.ANY, "REMOVE_SEARCHTERM_BUTTON")
  val UpdateLogFilePredicate: EventType[UpdateLogFilePredicate] = new EventType(Event.ANY, "UPDATE_LOGFILE_PREDICATE")
  val ScrollToActiveLogEntry: EventType[ScrollToActiveLogEntry] = new EventType(Event.ANY, "SCROLL_TO_ACTIVE_ENTRY")
  val ReoderSearchTermButtonEvent: EventType[ReoderSearchTermButtonEvent] = new EventType(Event.ANY, "REORDER_SEARCH_TERM")

case class DateFilterEvent(timeSettings: TimeSettings) extends Event(DataModelEvent.DateFilterEvent)

case class AddSearchTermButtonEvent(mutableSearchTerm: MutableSearchTerm) extends Event(DataModelEvent.AddSearchTermButtonEvent)

case class RemoveSearchTermButtonEvent(mutableSearchTerm: MutableSearchTerm) extends Event(DataModelEvent.RemoveSearchTermButtonEvent)

case class ReoderSearchTermButtonEvent(sourceSearchTerm: MutableSearchTerm, targetSearchTerm: MutableSearchTerm) extends Event(DataModelEvent.ReoderSearchTermButtonEvent)

class RemoveDateFilterEvent extends Event(DataModelEvent.RemoveDateFilterEvent)

class UpdateLogFilePredicate extends Event(DataModelEvent.UpdateLogFilePredicate)

class ScrollToActiveLogEntry extends Event(DataModelEvent.ScrollToActiveLogEntry)