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
  yield Settings(stageSettings, Map(), None, None, Seq(), None)

