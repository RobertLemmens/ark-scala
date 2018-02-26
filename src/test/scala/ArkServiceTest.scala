import cats.effect.Effect
import nl.robertlemmens.core.ArkService
import nl.robertlemmens.core.http.Http4sArkHttpImplementation
import nl.robertlemmens.core.models.{MainNet, Network}
import org.scalatest.FlatSpec

/**
  * Created by Robert Lemmens on 18-2-18.
  */
class ArkServiceTest extends FlatSpec {


  def MainNetFixture[F[_]](implicit E: Effect[F]) = new {
    val network = Network(MainNet)
    val httpImpl = Http4sArkHttpImplementation[F]
    val service = ArkService[F](httpImpl)
  }



}
