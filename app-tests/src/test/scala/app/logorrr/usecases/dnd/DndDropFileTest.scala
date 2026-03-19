package app.logorrr.usecases.dnd

import app.logorrr.usecases.StartEmptyApplicationTest
import app.logorrr.views.main.LogoRRRMain
import app.logorrr.{LogoRRRApp, TestFiles}
import javafx.stage.Stage
import org.junit.jupiter.api.Test

import scala.compiletime.uninitialized


class DndDropFileTest extends StartEmptyApplicationTest:

  var logorrrMain: LogoRRRMain = uninitialized

  @throws[Exception]
  override def start(stage: Stage): Unit =
    logorrrMain = LogoRRRApp.start(stage, services)


  @Test def startupEmpty(): Unit =
    checkForEmptyTabPane()

    TestFiles.seq.foreach:
      fileId => interacT(logorrrMain.logSource.processDraggedFiles(java.util.List.of(fileId.asPath.toFile)))

    expectCountOfOpenFiles(TestFiles.seq.size)





