package app.logorrr.model

import app.logorrr.clv.ElementSelector
import javafx.beans.property.SimpleIntegerProperty

case class LogEntrySelector(selectedLineNumberProperty: SimpleIntegerProperty) extends ElementSelector[LogEntry] {
  override def select(e: LogEntry): Unit = selectedLineNumberProperty.set(e.lineNumber)

}
