package nl.robertlemmens.core.algebra

import nl.robertlemmens.core.models._

/**
  * Created by Robert Lemmens on 1-2-18.
  */
trait ArkServiceAlgebra[F[_]] {

  def warmup(network: Network, numberOfPeers: Int = 20): F[Network]

  def getPeerStatus(nethash: String, peer: Peer): F[PeerStatus]

  def getPeerList(): F[List[Peer]]

  def getDelegates(): F[List[Delegate]]

  def getTransactions(account: Account, limit: Int): F[List[Transaction]]

  def postTransaction(transaction: Transaction): F[Transaction]

  def getFreshpeers(network: Network, numberOfPeers: Int): F[List[Peer]]

  def getPeersFromSeedList(network: Network, numberOfPeers: Int): F[List[Peer]]
}
