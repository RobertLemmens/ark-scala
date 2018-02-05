package nl.robertlemmens.core

import cats.effect.Effect
import nl.robertlemmens.core.algebra.ArkServiceAlgebra
import nl.robertlemmens.core.models._
import org.http4s.client.blaze._
import org.http4s.dsl.io._

import scala.language.higherKinds
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.{Header, Headers, Request, Uri, UrlForm}
import org.http4s.circe._

/**
  * Created by Robert Lemmens on 1-2-18.
  */
class Http4sArkService[F[_]: Effect] extends ArkServiceAlgebra[F] {

  //http1client has a connection pool and is pretty performant. The recommended client for http4s.
  val http1Client = Http1Client[F]()

  /**
    * Retrieve the list of peers we are going to use. Either fresh peers or from the seed list
    *
    */
  override def warmup(network: Network, numberOfPeers: Int = 20): F[Network] = { //todo move do upper class service.
    //if peers already exist on this network, just return it.
    if (network.peers.nonEmpty)
      network.pure[F]
    else {
      for {
        peerList <- getFreshpeers(network, numberOfPeers) // get fresh list of peers for this network
        seedPeerList <- getPeersFromSeedList(network, numberOfPeers)// get seed peers for this network
      } yield if(peerList.nonEmpty) network.copy(peers = peerList) else network.copy(peers = seedPeerList)
    }
  }

  override def getFreshpeers(network: Network, numberOfPeers: Int): F[List[Peer]] = {
    implicit val PeerResponseDecoder = jsonOf[F, PeerResponse]

    //if we have no providers, return an empty list
    if(network.peerListProviders.isEmpty)
      List[Peer]().pure[F]
    else {
      val mainProvider = network.peerListProviders.head
      http1Client.flatMap(_.expect[PeerResponse](mainProvider + "/api/peers").map(_.peers))
    }
  }

  override def getPeersFromSeedList(network: Network, numberOfPeers: Int): F[List[Peer]] = { //todo move do upper class service.
    val peerList = network.peerSeed.splitAt(numberOfPeers)._1
    peerList.map(e => Peer(
      e.split(":")(0),
      e.split(":")(1).toInt,
      network.version,
      0,
      "Unknown",
      0,
      "NEW",
      0)).pure[F]
  }

  override def getPeerStatus(nethash: String, peer: Peer): F[PeerStatus] = {
    implicit val PeerStatusHeaderDecoder = jsonOf[F, StatusHeader]
    implicit val PeerStatusResponseDecoder = jsonOf[F, PeerStatus]
    val protocol = if(peer.port == 4001) "http://" else "https://"
    val target = Uri.fromString(protocol+peer.ip+":"+peer.port+"/peer/status")
    val req: Request[F] =
      Request(uri = target.right.get)
        .withHeaders(
          Headers(
            Header("version", peer.version),
            Header("nethash", nethash),
            Header("port", peer.port.toString)
          )) // set headers todo: move convenient methods for these things
    http1Client.flatMap(_.expect[PeerStatus](req))
  }

  override def getDelegates(nethash: String, peer: Peer): F[DelegateResponse] = {
    implicit val DelegateResponseDecoder = jsonOf[F, DelegateResponse]
    val protocol = if(peer.port == 4001) "http://" else "https://"
    val target = Uri.fromString(protocol+peer.ip+":"+peer.port+"/api/delegates")
    val req: Request[F] = Request(uri = target.right.get).withHeaders(
      Headers(
        Header("version", peer.version),
        Header("nethash", nethash),
        Header("port", peer.port.toString)
      ))
    http1Client.flatMap(_.expect[DelegateResponse](req))
  }

  override def getTransactions(peer: Peer, nethash: String, limit: Int = 20): F[TransactionResponse] = {
    implicit val TransactionResponseDecoder = jsonOf[F, TransactionResponse]
    val protocol = if(peer.port == 4001) "http://" else "https://"
    val target = Uri.fromString(protocol+peer.ip+":"+peer.port+"/api/transactions")
    val req: Request[F] = Request(uri = target.right.get).withHeaders(
      Headers(
        Header("version", peer.version),
        Header("nethash", nethash),
        Header("port", peer.port.toString)
      ))
    http1Client.flatMap(_.expect[TransactionResponse](req))
  }

  override def postTransaction(nethash: String, transaction: Transaction, peer: Peer): F[Transaction] = {
    implicit val TransactionDecoder = jsonOf[F, Transaction]
    val protocol = if(peer.port == 4001) "http://" else "https://"
    val target = Uri.fromString(protocol+peer.ip+":"+peer.port+"/api/transactions")

    val req= POST

    http1Client.flatMap(_.expect[Transaction](""))
  }

}

object Http4sArkService {
  def apply[F[_]: Effect]: Http4sArkService[F] = new Http4sArkService[F]()
}