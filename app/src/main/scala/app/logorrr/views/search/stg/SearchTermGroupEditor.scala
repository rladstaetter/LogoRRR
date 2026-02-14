package app.logorrr.views.search.stg

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTerm}
import app.logorrr.model.FileIdPropertyHolder
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.binding.{Bindings, StringBinding}
import javafx.beans.property.ObjectPropertyBase
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.stage.{Modality, Stage, Window}

object SearchTermGroupEditor extends UiNodeFileIdAware:

  def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchTermGroupEditor])

  def mkScene(createStg: CreateStgUi
              , manageExistingSearchTermGroup: ManageStgEditor
              , closeButton: CloseStgEditorButton
              , titleBinding: StringBinding): Scene =
    val title: Label = new Label():
      setStyle("-fx-font-weight: bold")
      textProperty().bind(titleBinding)

    val hBox = new HBox(JfxUtils.mkHgrowFiller(), closeButton)
    val vbox = new VBox(10):
      VBox.setVgrow(this, Priority.ALWAYS)
      setPadding(new Insets(10))
      getChildren.addAll(title, createStg, manageExistingSearchTermGroup, hBox)
    new Scene(vbox, 960, 720)

class SearchTermGroupEditor(mutLogFileSettings: MutLogFileSettings
                            , fileId: FileId
                            , activeSearchTerms: Seq[SearchTerm])
  extends Stage with FileIdPropertyHolder:

  initModality(Modality.WINDOW_MODAL)
  setTitle("Edit Search Term Groups")

  lazy val createStg: CreateStgUi = new CreateStgUi(mutLogFileSettings, fileId, activeSearchTerms)
  lazy val manageExistingSearchTermGroup: ManageStgEditor = new ManageStgEditor(fileId)
  lazy val closeButton: CloseStgEditorButton = new CloseStgEditorButton(fileId, this)

  def init(window: Window, fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    initOwner(window)
     bindFileIdProperty(fileIdProperty)
    manageExistingSearchTermGroup.init(fileIdProperty, LogoRRRGlobals.getLogFileSettings(fileId).searchTermGroupEntries)
    createStg.init(fileIdProperty)
    val binding: StringBinding = Bindings.createStringBinding(() => "Search Term Groups " + getFileId.fileName, fileIdProperty)
    setScene(SearchTermGroupEditor.mkScene(createStg, manageExistingSearchTermGroup, closeButton, binding))

  def shutdown(): Unit = {
    manageExistingSearchTermGroup.shutdown()
    unbindFileIdProperty()
    createStg.shutdown()
  }