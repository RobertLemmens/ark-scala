package nl.robertlemmens.core

import cats._
import cats.data._
import cats.effect.Effect
import nl.robertlemmens.core.http.algebra.ArkHttpAlgebra
import nl.robertlemmens.core.models._

import scala.language.higherKinds
import cats.syntax.all._
/**
  * Created by Robert Lemmens
  */
class ArkService[F[_]: Effect](httpAlgebra: ArkHttpAlgebra[F]) {

  /**
    * Retrieve the list of peers we are going to use. Either fresh peers or from the seed list
    *
    */
  def warmup(network: Network, numberOfPeers: Int = 20): F[Network] = {

    //if peers already exist on this network, just return it.
    if (network.peers.nonEmpty)
      network.pure[F]
    else {
      for {
        peerList <- httpAlgebra.getFreshpeers(network, numberOfPeers) // get fresh list of peers for this network
        seedPeerList <- httpAlgebra.getPeersFromSeedList(network, numberOfPeers)// get seed peers for this network
      } yield if(peerList.nonEmpty) network.copy(peers = peerList) else network.copy(peers = seedPeerList)
    }
  }

  /**
    * Gets the status of a specific peer in the network.
    *
    * @param network
    * @param peer
    * @param M
    * @return
    */
  def getPeerStatus(network: Network, peer: Peer)(implicit M: Monad[F]): EitherT[F, ErrorCode, PeerStatus] = EitherT {
    httpAlgebra.getPeerStatus(network.netHash, peer).map {
      case None => Left(PeerNotFoundError)
      case Some(found) => Right(found)
    }
  }

  /**
    * Gets all the transactions from the peer
    *
    * @param network
    * @param peer
    * @param M
    * @return
    */
  def getTransactions(network: Network, peer: Peer)(implicit M: Monad[F]): EitherT[F, ErrorCode, List[Transaction]] = EitherT {
    httpAlgebra.getTransactions(network.netHash, peer).map{
      e => e.success match {
        case true => Right(e.transactions)
        case false => Left(FailureResponse)
      }
    }
  }

  /**
    * Gets all the transactions from a certain account(address)
    *
    * @param network
    * @param peer
    * @param account
    * @param M
    * @return
    */
  def getTransactionsFromAccount(network: Network, peer: Peer, account: Account)(implicit M: Monad[F]): EitherT[F, ErrorCode, List[Transaction]] = EitherT {
    httpAlgebra.getTransactionsFromAddress(account.address, network.netHash, peer).map {
      e => e.success match {
        case true => Right(e.transactions)
        case false => Left(FailureResponse)
      }
    }
  }

  /**
    * Gets a list of all the delegates
    *
    * @param network
    * @param peer
    * @param M
    * @return
    */
  def getDelegates(network: Network, peer: Peer)(implicit M: Monad[F]): EitherT[F, ErrorCode, List[DelegateM]] = EitherT {
    httpAlgebra.getDelegates(network.netHash, peer).map {
      e => e.success match {
        case true => Right(e.delegates)
        case false => Left(FailureResponse)
      }
    }
  }



}

object ArkService {
  def apply[F[_]: Effect](httpAlgebra: ArkHttpAlgebra[F]): ArkService[F] = new ArkService[F](httpAlgebra: ArkHttpAlgebra[F])
}