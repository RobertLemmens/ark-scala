package nl.robertlemmens.core.utils

import nl.robertlemmens.core.models.{ArkAddress, Network, SignedTransaction, Transaction}
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

  def secondSign(transaction: SignedTransaction, secondPassphrase: String): ECKey.ECDSASignature = {
    val txBytes = getBytes(transaction)
    signBytes(txBytes, secondPassphrase)
  }

  def verify(transaction: SignedTransaction): Boolean = {
    val keys = ECKey.fromPublicOnly(Base16.decode(transaction.senderPublicKey))
    val signature = Base16.decode(transaction.signature)
    val bytes = getBytes(transaction.tx)
    verifyBytes(bytes, signature, keys.getPubKey)
  }

  def secondVerify(transaction: SignedTransaction, secondPublicKey: String): Boolean = {
    val keys = ECKey.fromPublicOnly(Base16.decode(secondPublicKey))
    val signature = Base16.decode(transaction.secondSignature.get)
    val bytes = getBytes(transaction)

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

  def getId(transaction: SignedTransaction): String = {
    Base16.encode(Sha256Hash.hash(getBytes(transaction, false)))
  }

  def getKeys(passphrase: String): ECKey = {
    val sha256Hash = Sha256Hash.hash(passphrase.getBytes())
    ECKey.fromPrivate(sha256Hash, true)
  }

  private def getBytes(transaction: Transaction): Array[Byte] = {
    Transaction.toBytes(transaction)
  }

  private def getBytes(transaction: SignedTransaction, skipSecondSignature: Boolean = true): Array[Byte] = {
    SignedTransaction.toBytes(transaction,  skipSecondSignature)
  }

  private def signBytes(bytes: Array[Byte], passphrase: String): ECKey.ECDSASignature =  {
    val keys = getKeys(passphrase)
    keys.sign(Sha256Hash.of(bytes))
  }

  private def verifyBytes(bytes: Array[Byte], signature: Array[Byte], publicKey: Array[Byte]): Boolean = {
    ECKey.verify(Sha256Hash.hash(bytes), signature, publicKey)
  }





}
