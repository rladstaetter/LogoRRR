package app.logorrr.views.search.stg

import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm, SearchTermGroup}
import app.logorrr.model.{BoundId, DataModelEvent, OpenSettingsEditorEvent, SettingsEvent}
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.settings.SettingsEditor
import app.logorrr.views.util.{GfxElements, PulsatingAnimationTimer}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.{Button, Tooltip}
import javafx.stage.Window
import org.kordamp.ikonli.javafx.FontIcon

import java.time.Duration


object OpenSettingsDialogAndAddFavorites extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[OpenSettingsDialogAndAddFavorites])


class OpenSettingsDialogAndAddFavorites(activeSearchTerms: () => Seq[SearchTerm])
  extends Button with BoundId(OpenSettingsDialogAndAddFavorites.uiNode(_).value):

  private val favorites: Tooltip = GfxElements.ToolTips.mkAddToFavorites
  private val icon: FontIcon = GfxElements.Icons.settings
  private val iconDark: FontIcon = GfxElements.Icons.settingsDark
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
      LogoRRRGlobals.add(MutSearchTermGroup(SearchTermGroup("Untitled", activeSearchTerms(), false)))
      mkTimer().start()
      fireEvent(OpenSettingsEditorEvent())


  def init(window: Window, fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    bindIdProperty(fileIdProperty)

  def shutdown(): Unit = unbindIdProperty()
