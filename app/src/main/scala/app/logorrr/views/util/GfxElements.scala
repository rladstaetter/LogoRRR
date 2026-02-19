package app.logorrr.views.util

import javafx.scene.control.Tooltip
import org.kordamp.ikonli.fontawesome6.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

object GfxElements:

  object ToolTips:

    def mkRemove = new Tooltip("remove")

    def mkAddToFavorites = new Tooltip("add to favorites")

  object Icons:
    def edit = new FontIcon(FontAwesomeRegular.EDIT)

    def heart = new FontIcon(FontAwesomeRegular.HEART)

    def heartDark = new FontIcon(FontAwesomeSolid.HEART)

    def copy = new FontIcon(FontAwesomeRegular.COPY)

    def copyDark = new FontIcon(FontAwesomeSolid.COPY)

    def windowClose = new FontIcon(FontAwesomeRegular.WINDOW_CLOSE)

    def trash = new FontIcon(FontAwesomeSolid.TRASH)

    def clock = new FontIcon(FontAwesomeRegular.CLOCK)

    def plusSquare = new FontIcon(FontAwesomeRegular.PLUS_SQUARE)
    
    def playCircle = new FontIcon(FontAwesomeRegular.PLAY_CIRCLE)
    
    def playCircleDark = new FontIcon(FontAwesomeSolid.PLAY_CIRCLE)
    
    def search = new FontIcon(FontAwesomeSolid.SEARCH)
    
    def stop = new FontIcon(FontAwesomeRegular.STOP_CIRCLE)
