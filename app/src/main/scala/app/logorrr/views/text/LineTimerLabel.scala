package app.logorrr.views.text

import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

import java.time.Duration

object LineTimerLabel:

  def apply(duration: Duration): LineTimerLabel =
    val ldl = new LineTimerLabel
    ldl.setText(duration.toMillis.toString)
    ldl

class LineTimerLabel extends FontIcon(FontAwesomeSolid.CLOCK)