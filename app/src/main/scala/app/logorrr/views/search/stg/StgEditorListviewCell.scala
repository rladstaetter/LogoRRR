package app.logorrr.views.search.stg

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.io.FileId
import app.logorrr.views.search.st.SimpleSearchTermVis
import javafx.scene.control._
import javafx.scene.layout.{HBox, Priority, Region}


class StgEditorListviewCell(fileId: FileId) extends ListCell[StgEntry] {

  val deleteButton = DeleteStgButton(fileId)
  val globalStgButton = AddToGlobalSearchTermGroupListButton(fileId)

  override def updateItem(item: StgEntry, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    if (empty || item == null) {
      setText(null)
      setGraphic(null)
    } else {
      val label = new Label(item.name)
      label.setPrefWidth(100)

      // if the list is already contained in the global list, mark it as dark and disabled
      Option(item)
        .foreach(item => globalStgButton.setSelected(LogoRRRGlobals.searchTermGroupNames.contains(item.name)))

      // action when the 'X' button is clicked
      deleteButton.setOnAction(_ => {
        Option(getItem) match {
          case Some(stg) =>
            LogoRRRGlobals.getLogFileSettings(fileId).removeSearchTermGroup(stg.name)
          case None =>
        }
      })

      // action when 'heart' symbol is clicked
      globalStgButton.setOnAction(_ => {
        // item in list, remove on action
        if (globalStgButton.isSelected) {
          println("is selected")
          Option(getItem).foreach(i => {
            println("is REAAALYYY SELECTED : " + i.name)
            LogoRRRGlobals.putSearchTermGroup(i)
            println(LogoRRRGlobals.getSettings.searchTermGroups.keys)
          })
        } else {
          println("is not selected")
          Option(getItem).foreach(i => {
            println("is really not selected:" + i.name)
            LogoRRRGlobals.removeSearchTermGroup(i)
            println(LogoRRRGlobals.getSettings.searchTermGroups.keys)
          })
        }
      })


      // Use a region to push the button to the right
      val filler = new Region()
      HBox.setHgrow(filler, Priority.ALWAYS)
      val vis: Seq[SimpleToggleButton] = item.terms.map(t => new SimpleToggleButton(SimpleSearchTermVis(t)))

      val toolBar = new ToolBar
      toolBar.getItems.addAll(deleteButton, globalStgButton, label)
      toolBar.getItems.addAll(vis: _*)
      setGraphic(toolBar)
    }
  }


}
