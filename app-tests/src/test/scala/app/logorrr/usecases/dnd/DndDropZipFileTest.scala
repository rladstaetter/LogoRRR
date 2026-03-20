package app.logorrr.usecases.dnd

import app.logorrr.usecases.StartEmptyApplicationTest
import app.logorrr.views.main.LogoRRRMain
import app.logorrr.{LogoRRRApp, TestFiles}
import javafx.stage.Stage
import org.junit.jupiter.api.Test

import scala.compiletime.uninitialized

class DndDropZipFileTest extends StartEmptyApplicationTest:

  var logorrrMain: LogoRRRMain = uninitialized

  @throws[Exception]
  override def start(stage: Stage): Unit =
    logorrrMain = LogoRRRApp.start(stage, services)

  @Test def testOpeningAZipFileWith10Files(): Unit =
    checkForEmptyTabPane()

    val zipFile = TestFiles.zipFileContaining10Files.asPath.toFile

    interacT(logorrrMain.logSource.processDraggedFiles(java.util.List.of(zipFile)))

    expectCountOfOpenFiles(10)