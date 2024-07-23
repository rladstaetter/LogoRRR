package app.logorrr.services.file

import app.logorrr.io.FileId
import app.logorrr.views.menubar.LogoRRRFileChooser
import javafx.stage.Window

/**
 * File service which opens a native dialog.
 *
 * @param getWindow function to determine current window
 */
class DefaultFileService(getWindow: () => Window) extends FileService {

  override def openFile: Option[FileId] = {
    new LogoRRRFileChooser("Open log file").performShowAndWait(getWindow())
  }

}
