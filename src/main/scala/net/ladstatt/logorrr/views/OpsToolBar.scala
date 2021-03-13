package net.ladstatt.logorrr.views

import javafx.event.ActionEvent
import javafx.scene.control.{Button, Label, ToolBar}
import net.ladstatt.logorrr.LogReport


class OpsToolBar(logReport: LogReport) extends ToolBar {

  val openInApplicationButton = {
    val b = new Button("Open in Default Editor")
    b.setOnAction((_: ActionEvent) => {
      // not implemented since awt / Desktop is not supported atm in graal
    })
    b
  }

  getItems.addAll(new Label("Operations"), openInApplicationButton)
}
