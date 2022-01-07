package app.logorrr.util

import javafx.stage.FileChooser

import java.nio.file.Path

class LogoRRRFileChooser(title: String) {

  def showAndWait(): Option[Path] = {
    val fc = new FileChooser
    fc.setTitle(title)
    Option(fc.showOpenDialog(null)).map(_.toPath)
  }

}
