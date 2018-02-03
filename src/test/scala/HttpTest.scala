import cats.effect.IO
import nl.robertlemmens.core.Http4sArkService
import nl.robertlemmens.core.models.{DevNet, MainNet, Network}
import org.scalatest.FlatSpec

/**
  * Created by Robert Lemmens on 3-2-18.
  */
class HttpTest extends FlatSpec{

  def MainNetFixture = new {
    val network = Network(MainNet)
    val httpService = Http4sArkService[IO]
    val warmNetwork = httpService.warmup(network).unsafeRunSync()
  }

  "Warming up on the mainnet" should "give us a network with peers" in {
    val mainNet = MainNetFixture.warmNetwork

    assert(mainNet.peers.nonEmpty)
    assert(mainNet.peers.head.status == "OK")
  }

  "Getting a peer status on mainnet" should "give us a peerstatus success response" in {
    val mainNet = MainNetFixture.warmNetwork
    val peerStat = MainNetFixture.httpService.getPeerStatus(mainNet.netHash, mainNet.peers.head).unsafeRunSync()

    assert(peerStat.success)
  }

}
