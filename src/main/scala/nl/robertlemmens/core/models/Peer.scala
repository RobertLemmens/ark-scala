package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens on 1-2-18.
  */
case class Peer(ip: String, port: Int, version: String, errors: Int, os: String, height: Long, status: String, delay: Int)

//object Peer{
//  def apply(ip: String, port: Int, status: String): Peer = {
//    val protocol = if (port == 1443) "https://" else "http://"
//    new Peer(ip, port, protocol, status)
//  }
//}