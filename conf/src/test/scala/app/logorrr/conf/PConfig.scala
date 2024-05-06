
import pureconfig.*
import pureconfig.generic.derivation.default.*
import pureconfig.generic.auto.*

case class Port(number: Int)

sealed trait AuthMethod

case class Login(username: String, password: String) extends AuthMethod

case class Token(token: String) extends AuthMethod

case class PrivateKey(pkFile: java.io.File) extends AuthMethod

case class ServiceConf(
                        host: String,
                        port: Port,
                        useHttps: Boolean,
                        authMethods: List[AuthMethod]
                      )


@main
def main = {

  given ConfigReader[ServiceConf] = ConfigReader.derived[ServiceConf]
  given ConfigReader[AuthMethod] = ConfigReader.derived[AuthMethod]

  val res = ConfigSource.default.load[ServiceConf]
  println(res)
}
