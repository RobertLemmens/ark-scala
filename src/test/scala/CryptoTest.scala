import nl.robertlemmens.core.models.{DevNet, MainNet, Network}
import nl.robertlemmens.core.utils.CryptoUtils
import org.scalatest.FlatSpec

/**
  * Created by Robert Lemmens on 3-2-18.
  */
class CryptoTest extends FlatSpec{

  "Passphrase 'this is a top secret passphrase'" should "generate address 'AGeYmgbg2LgGxRW2vNNJvQ88PknEJsYizC' on Mainnet" in {
    val network = Network(MainNet)
    val keys = CryptoUtils.getKeys("this is a top secret passphrase")
    val address = CryptoUtils.getAddress(network, keys)

    assert(address == "AGeYmgbg2LgGxRW2vNNJvQ88PknEJsYizC")
  }

  it should "generate address 'D61mfSggzbvQgTUe6JhYKH2doHaqJ3Dyib' on Devnet" in {
    val network = Network(DevNet)
    val keys = CryptoUtils.getKeys("this is a top secret passphrase")
    val address = CryptoUtils.getAddress(network, keys)

    assert(address == "D61mfSggzbvQgTUe6JhYKH2doHaqJ3Dyib")
  }



}
