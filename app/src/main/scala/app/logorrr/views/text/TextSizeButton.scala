package app.logorrr.views.text

import app.logorrr.widgets.SymbolButton
import javafx.event.{ActionEvent, EventHandler}

/**
 * UI element for changing text size
 *
 * @param size size of 'T' icon
 * @param eventHandler event which will be triggered upon click
 */
class TextSizeButton(size: Int
                     , eventHandler: EventHandler[ActionEvent])
  extends SymbolButton(size, 'T', eventHandler)


