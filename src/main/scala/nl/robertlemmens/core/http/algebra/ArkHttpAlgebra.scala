package nl.robertlemmens.core.http.algebra

import nl.robertlemmens.core.models._

/**
  * Created by Robert Lemmens on 1-2-18.
  */
trait ArkHttpAlgebra[F[_]] {

  def getPeerStatus(nethash: String, peer: Peer): F[Option[PeerStatus]]

  def getDelegates(nethash: String, peer: Peer): F[DelegateResponse]

  def getTransactions(nethash: String, peer: Peer, limit: Int = 20): F[TransactionResponse]

  def getTransactionsFromAddress(address: String, netash: String, peer: Peer, limit: Int = 20): F[TransactionResponse]

  def postTransaction(nethash: String, transaction: Transaction, peer: Peer): F[Transaction]

  def getFreshpeers(network: Network, numberOfPeers: Int): F[List[Peer]]

  def getPeersFromSeedList(network: Network, numberOfPeers: Int): F[List[Peer]]
}
