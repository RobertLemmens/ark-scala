package nl.robertlemmens.core.http

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.{Decoder, HCursor}
import nl.robertlemmens.core.http.algebra.ArkHttpAlgebra
import nl.robertlemmens.core.models._
import org.http4s.circe._
import org.http4s.client.blaze._
import org.http4s.dsl.io._
import org.http4s.{Header, Headers, Method, Request, Status, Uri}

import scala.language.higherKinds

/**
  * Created by Robert Lemmens on 
  
  */
class Http4sArkHttpImplementation[F[_]: Effect] extends ArkHttpAlgebra[F] {

  //http1client has a connection pool and is pretty performant. The recommended client for http4s.
  val http1Client = Http1Client[F]()

  def warmup(network: Network, numberOfPeers: Int = 20): F[Network] = {

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

  override def getPeerStatus(nethash: String, peer: Peer): F[Option[PeerStatus]] = {
    implicit val PeerStatusHeaderDecoder = jsonOf[F, StatusHeader]
    implicit val PeerStatusResponseDecoder = jsonOf[F, Option[PeerStatus]]
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
    http1Client.flatMap(_.expect[Option[PeerStatus]](req))
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

  override def getTransactions(nethash: String, peer: Peer,  limit: Int = 20): F[TransactionResponse] = {
    implicit val TransactionDecoder: Decoder[SignedTransaction] = new Decoder[SignedTransaction] {
      final def apply(c: HCursor): Decoder.Result[SignedTransaction] = {
        for {
          id <- c.downField("id").as[String]
          typ <- c.downField("type").as[Int]
          timestamp <- c.downField("timestamp").as[Long]
          amount <- c.downField("amount").as[Long]
          fee <- c.downField("fee").as[Long]
          vendorField <- c.downField("vendorField").as[Option[String]]
          senderId <- c.downField("senderId").as[String]
          recipientId <- c.downField("recipientId").as[String]
          senderPubKey <- c.downField("senderPublicKey").as[String]
          signature <- c.downField("signature").as[String]
          signSignature <- c.downField("signSignature").as[Option[String]]
        } yield {
          val transactionType = typ match {
            case 0 => NORMAL
            case 1 => SECONDSIGNATURE
            case 2 => CREATEDELEGATE
            case 3 => VOTE
          }
          SignedTransaction(Transaction(Some(id), timestamp, Some(recipientId), amount, fee, transactionType, vendorField, None, None), signature,senderPubKey, signSignature)
        }
      }
    }

    implicit val TransactionResponseDecoder: Decoder[TransactionResponse] = new Decoder[TransactionResponse] {
      final def apply(c: HCursor): Decoder.Result[TransactionResponse] = {
        for {
          success <- c.downField("success").as[Boolean]
          transactions <- c.downField("transactions").as[List[SignedTransaction]]
          count <- c.downField("count").as[String]
        } yield {

          TransactionResponse(success, transactions, count)
        }
      }
    }

    implicit val test = jsonOf[F, TransactionResponse]

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

  override def getTransactionsFromAddress(address: String, netash: String, peer: Peer, limit: Int = 20): F[TransactionResponse] = {
    ???
  }

  override def postTransaction(nethash: String, transaction: Transaction, peer: Peer): F[Transaction] = {
    implicit val TransactionDecoder = jsonOf[F, Transaction]
    val protocol = if(peer.port == 4001) "http://" else "https://"
    val target = Uri.fromString(protocol+peer.ip+":"+peer.port+"/api/transactions")

    val req: Request[F] = Request(method = Method.POST, uri = target.right.get)

    http1Client.flatMap(_.fetch(req) {
      case Status.Successful(r) => r.attemptAs[Transaction].leftMap(_.message).value
     // case r => r.as[String].map(b => Left(s"Request $req failed with status ${r.status.code} and body $b"))
    })

    http1Client.flatMap(_.expect[Transaction](req))
  }

}

object Http4sArkHttpImplementation {
  def apply[F[_]: Effect]: Http4sArkHttpImplementation[F] = new Http4sArkHttpImplementation[F]()
}