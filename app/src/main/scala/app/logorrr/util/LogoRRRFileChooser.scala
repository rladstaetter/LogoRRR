package app.logorrr.util

import javafx.stage.{FileChooser, Window}

import java.nio.file.Path

class LogoRRRFileChooser(title: String) {

  def showAndWait(window: Window): Option[Path] = {
    val fc = new FileChooser
    fc.setTitle(title)
    Option(fc.showOpenDialog(window)).map(_.toPath)
  }

}
