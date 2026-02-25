package app.logorrr.model

import app.logorrr.clv.color.ColorPicker
import app.logorrr.conf.SearchTerm
import app.logorrr.conf.mut.MutLogFileSettings
import javafx.scene.paint.Color

class LogEntryPicker(settings: MutLogFileSettings) extends ColorPicker[LogEntry]:

  override def calc(e: LogEntry): Color = SearchTerm.calc(e.value, settings.activeSearchTermsBinding.get())

