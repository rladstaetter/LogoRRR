package app.logorrr.views.menubar

import app.logorrr.conf.{FileId, LogoRRRGlobals}
import javafx.stage.{FileChooser, Window}

/**
 * Helper class to group file open operation and setting LogoRRRGlobals
 *
 * @param title title of file dialog (no effect on mac?)
 */
class LogoRRRFileChooser(title: String):

  def performShowAndWait(window: Window): Option[FileId] =
    val fc = new FileChooser
    fc.setTitle(title)
    LogoRRRGlobals.getSomeLastUsedDirectory.foreach(d => fc.setInitialDirectory(d.toFile))
    val someFileId: Option[FileId] = Option(fc.showOpenDialog(window)).map(f => FileId(f.toPath))
    LogoRRRGlobals.setSomeLastUsedDirectory(someFileId.map(fileId => fileId.asPath.getParent))
    LogoRRRGlobals.persist(LogoRRRGlobals.getSettings)
    someFileId

