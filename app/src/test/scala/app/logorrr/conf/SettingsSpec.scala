package app.logorrr.conf

import app.logorrr.cp.TxtCp
import app.logorrr.io.SettingsFileIO
import net.ladstatt.util.log.TinyLog
import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpec
import upickle.default.*

import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success}

object SettingsSpec:

  val gen: Gen[Settings] = for
    stageSettings <- StageSettingsSpec.gen
  yield Settings(stageSettings, Map(), None, None, Map(), None)


class SettingsSpec extends AnyWordSpec with TinyLog {

  val cpResource = "/app/logorrr/conf/app.logorrr.conf"

  /** the point of this test is to show we can read an old style (pre 26.1.0) conf file without exception */
  "can read example config" in:
    TxtCp(cpResource).asString() match
      case Success(value) =>
        val migrated = SettingsMigrator.migrate(value)
        val settings = read[Settings](migrated)
        assert(settings.stageSettings.x == 192.0)
        val path = Paths.get("target/test.json")
        SettingsFileIO.toFile(settings, path)
        assert(Files.exists(path))
      case Failure(exception) =>
        logException(s"Could not load $cpResource", exception)
        fail()
}