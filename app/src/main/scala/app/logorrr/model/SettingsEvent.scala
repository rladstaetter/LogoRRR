package app.logorrr.model

import javafx.event.{Event, EventType}

object SettingsEvent:
  val OpenSettingsEditorEvent: EventType[OpenSettingsEditorEvent] = new EventType(Event.ANY, "OPEN_SETTINGS_EDITOR")

class OpenSettingsEditorEvent(val scrollToLast : Boolean) extends Event(SettingsEvent.OpenSettingsEditorEvent)
