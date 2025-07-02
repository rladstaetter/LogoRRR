package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.util.LogoRRRFonts
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Label, ToggleButton, Tooltip}
import javafx.scene.paint.Color
import net.ladstatt.util.os.OsUtil

object SearchActivateRegexToggleButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchActivateRegexToggleButton])

}

class SearchActivateRegexToggleButton(fileId: FileId)
  extends ToggleButton {
  setId(SearchActivateRegexToggleButton.uiNode(fileId).value)

  private val label = new Label("r")
  label.setStyle(LogoRRRFonts.jetBrainsMono(8))
  label.setTextFill(Color.DARKGREY)
  setGraphic(label)
  setTooltip(new Tooltip(
    s"""activate regular expression search
       |
       |shortcut: ${OsUtil.osFun("CTRL-R", "COMMAND-R", "CTRL-R")}""".stripMargin))

}
