package app.logorrr.util

import app.logorrr.conf.LogoRRRGlobals
import javafx.stage.{FileChooser, Window}

import java.nio.file.Path

class LogoRRRFileChooser(title: String) {

  def showAndWait(window: Window): Option[Path] = {
    val fc = new FileChooser
    fc.setTitle(title)
    LogoRRRGlobals.getSomeLastUsedDirectory.foreach(d => fc.setInitialDirectory(d.toFile))
    val somePath = Option(fc.showOpenDialog(window)).map(_.toPath)
    LogoRRRGlobals.setSomeLastUsedDirectory(somePath.map(_.getParent))
    LogoRRRGlobals.persist()
    somePath
  }

}
