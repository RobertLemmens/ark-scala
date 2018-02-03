package nl.robertlemmens.core.algebra

import nl.robertlemmens.core.models.{Account, Delegate, Peer, Transaction}

/**
  * Created by Robert Lemmens on 1-2-18.
  */
trait ArkServiceAlgebra[F[_]] {

  def getPeerStatus(peer: Peer): F[String]

  def getPeerList(): F[List[Peer]]

  def getDelegates(): F[List[Delegate]]

  def getTransactions(account: Account, limit: Int): F[List[Transaction]]

  def postTransaction(transaction: Transaction): F[Transaction]

}
