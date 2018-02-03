package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens
  * Asset type for a transaction. Might move this case class into the transaction file in a later stage
  */
case class Asset(votes: Option[List[String]], username: Option[String], signature: Option[String])
