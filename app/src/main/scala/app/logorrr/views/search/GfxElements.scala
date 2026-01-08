package app.logorrr.views.search

import javafx.scene.control.Tooltip
import org.kordamp.ikonli.fontawesome6.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

object GfxElements:

  def mkRemoveTooltip = new Tooltip("remove")
  def closeWindowIcon = new FontIcon(FontAwesomeRegular.WINDOW_CLOSE)
  def heartIcon = new FontIcon(FontAwesomeRegular.HEART)
  def heartDarkIcon = new FontIcon(FontAwesomeSolid.HEART)

