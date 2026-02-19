package app.logorrr.usecases.stg

import app.logorrr.TestFiles
import app.logorrr.conf.Settings
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

/**
 * - Open LogoRRR
 * - open fileId
 */
class StgHelloScenario1 extends SingleFileApplicationTest(TestFiles.simpleLog0) with FavoritesActions:

  @Test def scenario1(): Unit =
    openFile(fileId)
    clickOnFavoritesButton(fileId)


