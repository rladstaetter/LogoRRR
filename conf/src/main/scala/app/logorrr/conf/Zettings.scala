package app.logorrr.conf

import com.typesafe.config.{ConfigRenderOptions, ConfigValueFactory}
import pureconfig.*
import pureconfig.generic.derivation.default.*

import scala.jdk.CollectionConverters.*

case class Mo(x: Int) extends AnyVal

case class Foo(bar: String, baz: Int)

@main
def showWriter(): Unit = {

  /*
    given fooWriter: ConfigWriter[Foo] = ConfigWriter.fromFunction: foo =>
      ConfigValueFactory.fromMap(
        Map(
          "bar" -> foo.bar,
          "baz" -> foo.baz
        ).asJava
      )

    println(
      fooWriter.to(Foo(bar = "bar", baz = 1)).render()
    )*/
}