package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens
  * 
  */
case class Delegate(username: String, address: String, publicKey: String, vote: String, producedblocks: Long, missedblocks: Long, rate: Int, approval: Double, productivity: Double) //suffixed M for now
