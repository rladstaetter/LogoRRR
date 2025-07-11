package app.logorrr.jfxbfr

import app.logorrr.model.LogEntry
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}

case class LogEntryVizor(selectedLineNumberProperty: SimpleIntegerProperty
                         , widthProperty: ReadOnlyDoubleProperty
                         , blockSizeProperty: SimpleIntegerProperty
                         , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                         , lastVisibleTextCellIndexProperty: SimpleIntegerProperty) extends Vizor[LogEntry] {
  /** returns true if entry is active (= selected) - typically this entry is highlighted in some form */
  def isSelected(e: LogEntry): Boolean = e.lineNumber == selectedLineNumberProperty.getValue

  /** element is the first visible element in the text view (the start of the visible elements) */
  def isFirstVisible(e: LogEntry): Boolean = e.lineNumber == firstVisibleTextCellIndexProperty.get()

  /** element is the last visible element in the text view (the end of the visible elements) */
  def isLastVisible(e: LogEntry): Boolean = e.lineNumber == lastVisibleTextCellIndexProperty.get()

  /** element is visible in the text view */
  def isVisibleInTextView(logEntry: LogEntry): Boolean = {
    firstVisibleTextCellIndexProperty.get() < logEntry.lineNumber && logEntry.lineNumber < lastVisibleTextCellIndexProperty.get()
  }

}
