package app.logorrr.views.util

import javafx.scene.control.Tooltip
import org.kordamp.ikonli.fontawesome6.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

object GfxElements:

  object ToolTips:

    def mkColorPicker = new Tooltip("change color")

    def mkRemove = new Tooltip("remove")

    def mkDragToReorder = new Tooltip("drag to reorder")

    def mkAddToFavorites = new Tooltip("add to favorites")

  object Icons:
    def listAlt = new FontIcon(FontAwesomeRegular.LIST_ALT)

    def listAltDark = new FontIcon(FontAwesomeRegular.LIST_ALT)

    def edit = new FontIcon(FontAwesomeRegular.EDIT)

    def editDark = new FontIcon(FontAwesomeRegular.EDIT)

    def star = new FontIcon(FontAwesomeRegular.SUN)

    def starDark = new FontIcon(FontAwesomeSolid.SUN)

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
