package nl.robertlemmens.core.utils

import nl.robertlemmens.core.models.{ArkAddress, Network, Transaction}
import org.bitcoinj.core._
import org.spongycastle.crypto.digests.RIPEMD160Digest
import scorex.crypto.encode.Base16
/**
  * Some implementations for cryptographic functions needed by the api
  */
object CryptoUtils {

  def sign(transaction: Transaction, passphrase: String): ECKey.ECDSASignature = {
    val txBytes = getBytes(transaction)
    signBytes(txBytes, passphrase)
  }

  def secondSign(transaction: Transaction, secondPassphrase: String): ECKey.ECDSASignature = {
    val txBytes = getBytes(transaction, false)
    signBytes(txBytes, secondPassphrase)
  }

  def verify(transaction: Transaction): Boolean = {
    val keys = ECKey.fromPublicOnly(Base16.decode(transaction.senderPublicKey.get))
    val signature = Base16.decode(transaction.signature.get)
    val bytes = getBytes(transaction)
    verifyBytes(bytes, signature, keys.getPubKey)
  }

  def secondVerify(transaction: Transaction, secondPublicKey: String): Boolean = {
    val keys = ECKey.fromPublicOnly(Base16.decode(secondPublicKey))
    val signature = Base16.decode(transaction.signSignature.get)
    val bytes = getBytes(transaction, false)

    verifyBytes(bytes, signature, keys.getPubKey)
  }

  def getAddress(network: Network, keys: ECKey): String = {
    getAddress(network, keys.getPubKey)
  }

  def getAddress(network: Network, pubKey: Array[Byte]): String = {
    val digest = new RIPEMD160Digest()
    digest.update(pubKey, 0, pubKey.length)
    val out = new Array[Byte](20)
    digest.doFinal(out, 0)
    new ArkAddress(network.prefix, out).toBase58
  }

  def getId(transaction: Transaction): String = {
    Base16.encode(Sha256Hash.hash(getBytes(transaction, false, false)))
  }

  def getKeys(passphrase: String): ECKey = {
    val sha256Hash = Sha256Hash.hash(passphrase.getBytes())
    ECKey.fromPrivate(sha256Hash, true)
  }

  private def getBytes(transaction: Transaction, skipSignature: Boolean = true, skipSecondSignature: Boolean = true): Array[Byte] = {
    Transaction.toBytes(transaction, skipSignature, skipSecondSignature)
  }

  private def signBytes(bytes: Array[Byte], passphrase: String): ECKey.ECDSASignature =  {
    val keys = getKeys(passphrase)
    keys.sign(Sha256Hash.of(bytes))
  }

  private def verifyBytes(bytes: Array[Byte], signature: Array[Byte], publicKey: Array[Byte]): Boolean = {
    ECKey.verify(Sha256Hash.hash(bytes), signature, publicKey)
  }





}
