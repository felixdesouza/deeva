import java.io.File

import play.api.Logger
import play.core.server.NettyServer

object Deeva extends App {
  println("Initialising my own play through Deeva! :D")
  NettyServer.createServer(new File(""))
}
