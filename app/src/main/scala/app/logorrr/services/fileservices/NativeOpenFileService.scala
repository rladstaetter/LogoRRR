package app.logorrr.services.fileservices

import app.logorrr.views.menubar.LogoRRRFileChooser
import javafx.stage.Window

import java.nio.file.Path

class NativeOpenFileService(getWindow: () => Window) extends LogoRRRFileOpenService {
  override def openFile: Option[Path] = {
    new LogoRRRFileChooser("Open log file").showAndWait(getWindow())
  }
}
