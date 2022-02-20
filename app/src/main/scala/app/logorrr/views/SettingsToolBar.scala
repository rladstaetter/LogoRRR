package app.logorrr.views

import javafx.scene.control.{Button, Label, ToolBar}

/* container to host settings button, positioned to the right of the application / separate for each log view */
class SettingsToolBar extends ToolBar {
  getItems.add(new Button("Settings"))
}
