package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens on 1-2-18.
  */
case class Account(address: String, publicKey: String, balance: BigDecimal, username: String, vote: BigDecimal, votes: List[BigDecimal], rate: Int)
