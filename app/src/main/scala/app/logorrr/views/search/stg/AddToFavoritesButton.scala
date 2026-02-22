package app.logorrr.views.search.stg

import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm, SearchTermGroup}
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.util.{GfxElements, PulsatingAnimationTimer}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.{Button, Tooltip}
import javafx.stage.Window
import org.kordamp.ikonli.javafx.FontIcon

import java.time.Duration


object AddToFavoritesButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[AddToFavoritesButton])


class AddToFavoritesButton(activeSearchTerms: () => Seq[SearchTerm])
  extends Button with BoundId(AddToFavoritesButton.uiNode(_).value):

  private val favorites: Tooltip = GfxElements.ToolTips.mkAddToFavorites
  private val icon: FontIcon = GfxElements.Icons.star
  private val iconDark: FontIcon = GfxElements.Icons.starDark
  setGraphic(icon)
  setTooltip(favorites)


  def mkTimer() = new PulsatingAnimationTimer(this
    , icon
    , iconDark
    , favorites
    , favorites.getText
    , Duration.ofSeconds(1))

  setOnAction:
    _ =>
      LogoRRRGlobals.add(MutSearchTermGroup(SearchTermGroup("", activeSearchTerms(), false)))
      mkTimer().start()


  def init(window: Window, fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    bindIdProperty(fileIdProperty)

  def shutdown(): Unit = unbindIdProperty()
