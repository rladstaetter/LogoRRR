package app.logorrr.views.search.stg

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.util.JfxUtils
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.stage.{Modality, Stage, Window}

object SearchTermGroupEditor {

}

class SearchTermGroupEditor(owner: Window
                            , mutLogFileSettings: MutLogFileSettings
                            , fileId: FileId
                            , activeSearchTerms: Seq[SearchTerm]) extends Stage:
  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle("Edit Search Term Groups")

  private val title: Label =
    val l = new Label(s"Search Term Groups: ${fileId.fileName}")
    l.setStyle("-fx-font-weight: bold")
    l

  private val createStg = new CreateStgUi(mutLogFileSettings, fileId, activeSearchTerms)
  private val manageExistingSearchTermGroup = ManageStgEditor(fileId)

  private val closeButton: Button = new CloseStgEditorButton(fileId, this)
  private val hBox = new HBox(JfxUtils.mkHgrowFiller(), closeButton)

  private val vbox = new VBox(10)
  VBox.setVgrow(vbox, Priority.ALWAYS)
  vbox.setPadding(new Insets(10))


  vbox.getChildren.addAll(title, createStg, manageExistingSearchTermGroup, hBox)


  // --- Final Setup ---
  setScene(new Scene(vbox, 800, 600)) // Adjusted size for list view
