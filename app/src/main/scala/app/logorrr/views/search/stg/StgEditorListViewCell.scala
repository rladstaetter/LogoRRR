package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.views.search.st.SimpleSearchTermVis
import javafx.scene.control._


class StgEditorListViewCell(fileId: FileId) extends ListCell[SearchTermGroup] {

  val deleteButton = DeleteStgButton(fileId)
  val globalStgButton = AddToGlobalSearchTermGroupListButton(fileId)

  override def updateItem(item: SearchTermGroup, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      val label = new Label(item.name)
      label.setPrefWidth(100)


      // action when the 'X' button is clicked
      deleteButton.setOnAction(_ => {
        Option(getItem) match {
          case Some(stg) =>
            getListView.getItems.remove(stg)
            LogoRRRGlobals.getLogFileSettings(fileId).removeSearchTermGroup(stg.name)
          case None =>
        }
      })

      // if the list is already contained in the global list, mark it as selected
      Option(item)
        .foreach(item => globalStgButton.setSelected(LogoRRRGlobals.searchTermGroupNames.contains(item.name)))

      // action when 'heart' symbol is clicked
      globalStgButton.setOnAction(_ => {
        // item in list, remove on action
        if (globalStgButton.isSelected) {
          Option(getItem).foreach(i => LogoRRRGlobals.putSearchTermGroup(i))
        } else {
          Option(getItem).foreach(i => LogoRRRGlobals.removeSearchTermGroup(i.name))
        }
      })

      // Use a region to push the button to the right
      val vis: Seq[SimpleToggleButton] = item.terms.map(t => new SimpleToggleButton(SimpleSearchTermVis(t)))

      val toolBar = new ToolBar
      toolBar.getItems.addAll(deleteButton, globalStgButton, label)
      toolBar.getItems.addAll(vis: _*)
      setGraphic(toolBar)
    }
  }


}





