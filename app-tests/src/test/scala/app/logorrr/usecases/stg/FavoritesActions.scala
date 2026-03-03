package app.logorrr.usecases.stg

import app.logorrr.conf.FileId
import app.logorrr.usecases.FxBaseInterface
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.search.stg.*

trait FavoritesActions:
  self: FxBaseInterface =>

  def clickOnFavoritesButton(fileId: FileId): Unit =
    waitAndClick(OpenSettingsDialogAndAddFavorites.uiNode(fileId))
    waitAndClick(SettingsEditor.CloseButton)
