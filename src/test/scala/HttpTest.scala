import cats.effect.IO
import nl.robertlemmens.core.http.Http4sArkHttpImplementation
import nl.robertlemmens.core.models.{DevNet, MainNet, Network}
import org.scalatest.FlatSpec

/**
  * Created by Robert Lemmens on 3-2-18.
  */
class HttpTest extends FlatSpec{

  def MainNetFixture = new {
    val mainNet = Network(MainNet)
    val devNet = Network(DevNet)
    val httpService = Http4sArkHttpImplementation[IO]
    val warmMainNetwork = httpService.warmup(mainNet).unsafeRunSync()
    val warmDevNetwork = httpService.warmup(devNet).unsafeRunSync()

  }

  "Warming up on the mainnet" should "give us a network with peers" in {
    val mainNet = MainNetFixture.warmMainNetwork

    assert(mainNet.peers.nonEmpty)
    assert(mainNet.peers.head.status == "OK")
  }

  "Getting a peer status on mainnet" should "give us a peerstatus success response" in {
    val mainNet = MainNetFixture.warmMainNetwork
    val peerStat = MainNetFixture.httpService.getPeerStatus(mainNet.netHash, mainNet.peers.head).unsafeRunSync()

    assert(peerStat.get.success)
  }

  "Getting a random peer on the mainnet" should "give us a random peer" in {
    val mainnet = MainNetFixture.warmMainNetwork
    val randomPeer = Network.getRandomPeer(mainnet)

    randomPeer match {
      case Right(peer) => assert(true) //Right pass de test
      case Left(error) => assert(false)
    }
  }

  it should "give a left error when peers are empty" in {
    val emptyNetwork = Network(MainNet)
    val randomPeer = Network.getRandomPeer(emptyNetwork)

    randomPeer match {
      case Right(peer) => assert(false)
      case Left(error) => assert(true) //Left pass the test
    }
  }

  "Getting a list of delegates on the mainnet" should "give us a list of delegates with success flag" in {
    val mainnet = MainNetFixture.warmMainNetwork
    val httpService = MainNetFixture.httpService

    Network.getRandomPeer(mainnet) match {
      case Right(peer) => assert(httpService.getDelegates(mainnet.netHash, peer).unsafeRunSync().success) //pass the test
      case Left(error) => assert(false) //fail the test
    }

  }

  "Calling the transactions endpoint" should "give us a list of transactions" in {
    val mainnet = MainNetFixture.warmMainNetwork
    val httpService = MainNetFixture.httpService

    Network.getRandomPeer(mainnet) match {
      case Right(peer) => assert(httpService.getTransactions(mainnet.netHash,peer).unsafeRunSync().success)
      case Left(error) => assert(false)
    }
  }

  "Getting a peer on devnet" should "give us a peer to get transactions from" in {
    val devNet = MainNetFixture.warmDevNetwork
    val httpService = MainNetFixture.httpService

    Network.getRandomPeer(devNet) match {
      case Right(peer) => assert(httpService.getTransactions(devNet.netHash, peer).unsafeRunSync().success)
      case Left(error) => assert(false)
    }
  }

}
