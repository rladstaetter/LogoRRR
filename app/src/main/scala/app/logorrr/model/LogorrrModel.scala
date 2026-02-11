package app.logorrr.model

import app.logorrr.conf.mut.MutLogFileSettings
import javafx.collections.ObservableList

class LogorrrModel(val mutLogFileSettings: MutLogFileSettings
                   , val entries: ObservableList[LogEntry])
