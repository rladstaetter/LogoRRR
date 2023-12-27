import com.typesafe.config.ConfigRenderOptions
import pureconfig.ConfigReader.Result
import pureconfig.*
import pureconfig.generic.derivation.default.*

@main def jo(): Unit = {
  sealed trait AnimalConf derives ConfigReader
  case class DogConf(age: Int) extends AnimalConf
  case class BirdConf(canFly: Boolean) extends AnimalConf

  val renderOptions: ConfigRenderOptions = ConfigRenderOptions.defaults().setOriginComments(false)

  ConfigSource.string("{ type: dog-conf, age: 4 }").load[AnimalConf] match
    case Left(value) => println("error")
    case Right(value) =>
      val uhu = ConfigWriter[AnimalConf].to(value).render(renderOptions)

}
