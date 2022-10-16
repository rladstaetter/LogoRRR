package app.logorrr.model

import app.logorrr.views.block.BlockView
import javafx.scene.paint.Color

import java.time.Instant
import scala.language.postfixOps

/**
 * represents one line in a log file
 *
 * @param lineNumber line number of this log entry
 * @param value contens of line in plaintext
 * @param someInstant a timestamp if there is any
 * */
case class LogEntry(lineNumber: Int
                    , color: Color // remove, should be calculated because of given filters
                    , value: String
                    , someInstant: Option[Instant])
  extends BlockView.E
