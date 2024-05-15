package app.logorrr.usecases.search

import app.logorrr.TestFiles
import app.logorrr.io.FileId
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.{SearchButton, SearchTextField}
import org.junit.jupiter.api.Test

class SimpleSearchTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  @Test def search(): Unit = {
    openFile(path)
    searchFor("1")
    searchFor("2")
    searchFor("3")
    searchFor("4")
    searchFor("0")
  }

  private def searchFor(needle: String) = {
    clickOn(SearchTextField.uiNode(FileId(path)).ref).write(needle)
    clickOn(SearchButton.uiNode(FileId(path)).ref)
  }
}
