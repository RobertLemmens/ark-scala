import cats.effect.IO
import nl.robertlemmens.core.ArkService
import nl.robertlemmens.core.http.Http4sArkHttpImplementation
import nl.robertlemmens.core.models.{MainNet, Network}
import org.scalatest.FlatSpec

/**
  * Created by Robert Lemmens on 18-2-18.
  */
class ArkServiceTest extends FlatSpec {

  def MainNetFixture = new {
    val network = Network(MainNet)
    val httpImpl = Http4sArkHttpImplementation[IO]
    val service = ArkService[IO](httpImpl)
  }

}
