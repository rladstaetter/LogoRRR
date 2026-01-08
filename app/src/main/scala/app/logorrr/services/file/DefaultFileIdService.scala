package app.logorrr.services.file

import app.logorrr.conf.FileId
import app.logorrr.views.menubar.LogoRRRFileChooser
import javafx.stage.Window

/**
 * File service which opens a native dialog.
 *
 * @param getWindow function to determine current window
 */
class DefaultFileIdService(getWindow: () => Window) extends FileIdService:

  override def provideFileId: Option[FileId] =
    new LogoRRRFileChooser("Open log file").performShowAndWait(getWindow())

