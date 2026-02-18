package app.logorrr.views.search.st

import app.logorrr.model.LogEntry

import java.util.function.Predicate

class IsActiveAndContainsPredicate(tb: ASearchTermToggleButton) extends Predicate[LogEntry] {
  override def test(entry: LogEntry): Boolean =
    tb.isSelected && (Option(tb.valueProperty.get()) match {
      case Some(value) =>
        entry.value.contains(value)
      case None => false
    })

}
