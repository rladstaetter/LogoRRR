package app.logorrr.model

import app.logorrr.clv.color.ColorPicker
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.views.search.SearchTerm
import javafx.scene.paint.Color

class LogEntryPicker(settings: MutLogFileSettings) extends ColorPicker[LogEntry] {

  var searchTerms: Set[SearchTerm] = settings.getSearchTerms.toSet

  override def calc(e: LogEntry): Color = SearchTerm.calc(e.value, searchTerms)

  override def init(): Unit = {
    searchTerms = settings.getSearchTerms.toSet
  }
}
