package app.logorrr.views.settings

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.SearchTermGroup
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.search.stg.{DeleteStgButton, SearchTermLabel}
import javafx.scene.control.{Label, ListCell, ToolBar}


class SettingsStgListViewCell extends ListCell[SearchTermGroup]:
  setId(SettingsEditor.SettingsStgListView.value)

  val deleteButton = new DeleteStgButton(SettingsEditor.SettingsStgListViewDelete)

  override def updateItem(item: SearchTermGroup, empty: Boolean): Unit =
    super.updateItem(item, empty)
    if empty || item == null then
      setText(null)
      setGraphic(null)
    else
      val label = new Label(item.name)
      label.setPrefWidth(100)

      deleteButton.setOnAction(_ => {
        Option(getItem) match {
          case Some(stg) =>
            getListView.getItems.remove(stg)
            LogoRRRGlobals.removeSearchTermGroup(stg.name)
          case None =>
        }
      })

      val vis: Seq[SearchTermLabel] = item.terms.map(t => SearchTermLabel(t))

      val toolBar = new ToolBar
      toolBar.getItems.addAll(deleteButton, label)
      toolBar.getItems.addAll(vis*)
      setGraphic(toolBar)


