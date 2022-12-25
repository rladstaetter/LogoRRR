package app.logorrr.views.text

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

import java.time.Instant

object LineTimerLabel {

  def apply(instant: Instant): LineTimerLabel = {
    val ldl = new LineTimerLabel
    ldl
  }
}

class LineTimerLabel extends FontIcon(FontAwesomeSolid.CLOCK)