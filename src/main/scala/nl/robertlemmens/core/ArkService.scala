package nl.robertlemmens.core

import nl.robertlemmens.core.http.algebra.ArkHttpAlgebra

/**
  * Created by Robert Lemmens on 6-2-18.
  */
class ArkService[F[_]](httpAlgebra: ArkHttpAlgebra[F]) {

}

object ArkService {
  def apply[F[_]](httpAlgebra: ArkHttpAlgebra[F]): ArkService[F] = new ArkService[F](httpAlgebra: ArkHttpAlgebra[F])
}