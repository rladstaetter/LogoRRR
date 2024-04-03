package app.logorrr.usecases

import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenMultipleFilesService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{CanOpenFile, CanStartApplication}

import java.nio.file.Path

/**
 * Extend this class if you have tests which involve more than one file
 *
 * @param files files which are supported for this test
 */
class MultipleFileApplicationTest(val files: Seq[Path])
  extends TestFxBaseApplicationTest
    with CanStartApplication
    with CanOpenFile {

  val services = LogoRRRServices(new MockHostServices
    , new OpenMultipleFilesService(files)
    , isUnderTest = true)

}
