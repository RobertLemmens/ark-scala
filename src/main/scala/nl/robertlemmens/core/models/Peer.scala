package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens
  *
  * A peer represents one peer(one ip address) that is callable(or not, if the connection is bad).
  */
case class Peer(ip: String, port: Int, version: String, errors: Int, os: String, height: Long, status: String, delay: Int)
