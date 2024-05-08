package app.logorrr.conf

import javafx.scene.paint.Color
import org.scalacheck.{Gen, Prop}
import org.scalatest.wordspec.AnyWordSpec
import pureconfig.ConfigSource

import scala.util.Random

object LogoRRRColorSpec {

  val gen: Gen[Color] = Gen.const(Color.color(Random.nextDouble()
    , Random.nextDouble()
    , Random.nextDouble()
    , Random.nextDouble()
  ))

}

class LogoRRRColorSpec extends CheaterSpec {

  import LogoRRRColor.{reader, writer}

  "test reader" in {
    val jos =
      """|{
         |    "color" : "0xc999296a"
         |}
         |""".stripMargin

    val jo = """{ "mumu" : 1 }"""
    ConfigSource.string(jo).load[Color] match
      case Left(value) => println(value)
      case Right(value) => println(value)
  }

  "de/serialize" in {
    check(Prop.forAll(LogoRRRColorSpec.gen) {
      col =>
        val str = LogoRRRColor.writer.to(col).render(RenderOptions.opts)
        println(str)
        ConfigSource.string(str).load[Color] match {
          case Right(value) => value == col
          case Left(value) => false
        }
    })
  }

}
