package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens
  */
case class Account(address: String, publicKey: String, balance: BigDecimal, username: String, vote: BigDecimal, votes: List[BigDecimal], rate: Int)
