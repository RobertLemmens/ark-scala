package nl.robertlemmens.core.models

import org.bitcoinj.core.VersionedChecksummedBytes

/**
  * Created by Robert Lemmens on 2-2-18.
  */
class ArkAddress(networkPrefix: Int, pubKey: Array[Byte]) extends VersionedChecksummedBytes(networkPrefix, pubKey) {

}
