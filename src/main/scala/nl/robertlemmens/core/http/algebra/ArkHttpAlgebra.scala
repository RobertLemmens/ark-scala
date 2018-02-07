package nl.robertlemmens.core.http.algebra

import nl.robertlemmens.core.models._

/**
  * Created by Robert Lemmens on 1-2-18.
  */
trait ArkHttpAlgebra[F[_]] {

  def warmup(network: Network, numberOfPeers: Int = 20): F[Network]

  def getPeerStatus(nethash: String, peer: Peer): F[PeerStatus]

  def getDelegates(nethash: String, peer: Peer): F[DelegateResponse]

  def getTransactions(peer: Peer, nethash: String, limit: Int = 20): F[TransactionResponse]

  def getTransactionsFromAddress(address: String, netash: String, peer: Peer, limit: Int = 20): F[TransactionResponse]

  def postTransaction(nethash: String, transaction: Transaction, peer: Peer): F[Transaction]

  def getFreshpeers(network: Network, numberOfPeers: Int): F[List[Peer]]

  def getPeersFromSeedList(network: Network, numberOfPeers: Int): F[List[Peer]]
}
