package app.logorrr.model

import app.logorrr.clv.ElementSelector
import javafx.beans.property.{SetPropertyBase, SimpleIntegerProperty}

case class LogEntrySelector(sharedElementSelection : SetPropertyBase[Int]) extends ElementSelector[LogEntry]:

  override def select(e: LogEntry): Unit = {
    sharedElementSelection.add(e.lineNumber)
  }

