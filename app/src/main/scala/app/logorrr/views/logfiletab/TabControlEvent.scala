package app.logorrr.views.logfiletab

import javafx.event.{Event, EventType}

class TabControlEvent(eventType: EventType[? <: Event]) extends Event(eventType)

object TabControlEvent {
  val CloseSelectedTab: EventType[TabControlEvent] = new EventType(Event.ANY, "CLOSE_SELECTED")
  val CloseOthers: EventType[TabControlEvent] = new EventType(Event.ANY, "CLOSE_OTHERS")
  val CloseRight: EventType[TabControlEvent] = new EventType(Event.ANY, "CLOSE_RIGHT")
  val CloseLeft: EventType[TabControlEvent] = new EventType(Event.ANY, "CLOSE_LEFT")
  val CloseAll: EventType[TabControlEvent] = new EventType(Event.ANY, "CLOSE_ALL")
  val OpenInFinder: EventType[TabControlEvent] = new EventType(Event.ANY, "OPEN_IN_FINDER")
}