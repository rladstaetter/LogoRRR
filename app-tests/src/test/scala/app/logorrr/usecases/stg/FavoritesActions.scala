package app.logorrr.usecases.stg

import app.logorrr.conf.FileId
import app.logorrr.steps.ChoiceBoxActions
import app.logorrr.usecases.{FxBaseInterface, MultipleFileApplicationTest, SingleFileApplicationTest}
import app.logorrr.views.search.st.{RemoveSearchTermButton, ASearchTermToggleButton, SearchTermToolBar}
import app.logorrr.views.search.stg.*
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import org.testfx.api.FxRobotInterface

trait FavoritesActions:
  self: FxBaseInterface =>

  def clickOnFavoritesButton(fileId: FileId): Unit =
    waitForVisibility(AddToFavoritesButton.uiNode(fileId))
    clickOn(AddToFavoritesButton.uiNode(fileId))
