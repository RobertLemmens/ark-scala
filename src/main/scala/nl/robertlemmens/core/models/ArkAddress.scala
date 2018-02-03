package nl.robertlemmens.core.models

import org.bitcoinj.core.VersionedChecksummedBytes

/**
  * Created by Robert Lemmens
  *
  * A Ark address. Need create this due to the protected nature of the VersionCheckSummedBytes we use.
  */
class ArkAddress(networkPrefix: Int, pubKey: Array[Byte]) extends VersionedChecksummedBytes(networkPrefix, pubKey) {

}
