package nl.robertlemmens.core

import nl.robertlemmens.core.models._
import nl.robertlemmens.core.utils.{CryptoUtils, TimeUtils}
import scorex.crypto.encode.Base16

/**
  * Created by Robert Lemmens on 2-2-18.
  */
class TransactionBuilder {

  private def signSignature(transaction: Transaction, passphrase: String): Option[Transaction] = {
    Option(transaction.copy(signature = Some(Base16.encode(CryptoUtils.sign(transaction, passphrase).encodeToDER()))))
  }

  private def signSenderPublicKey(passphrase: String): String = {
    Base16.encode(CryptoUtils.getKeys(passphrase).getPubKey)
  }

  private def sign(transaction: Transaction, passphrase: String): Option[Transaction] = {
    signSignature(transaction.copy( senderPublicKey = Some(signSenderPublicKey(passphrase))), passphrase)
  }

  private def secondSign(transaction: Transaction, secondPassphrase: String): Option[Transaction] = {
    Option(transaction.copy(signSignature = Some(Base16.encode(CryptoUtils.secondSign(transaction, secondPassphrase).encodeToDER()))))
  }

  def createTransaction(recipientId: String, satoshiAmount: Long, vendorField: Option[String], passphrase: String, secondPassphrase: Option[String]): Option[Transaction] = {
    val timeStamp = TimeUtils.getTime()
    val signedTransaction = sign(Transaction(None, timeStamp, Some(recipientId), satoshiAmount, 10000000, NORMAL, vendorField, None, None, None, None, None), passphrase)
    val finalTransaction = secondPassphrase match {
      case Some(x) => secondSign(signedTransaction.get, x)
      case None => signedTransaction
    }
    finalTransaction.map(e => e.copy(id = Some(CryptoUtils.getId(e))))
  }

  def createVote(network: Network, votes: List[String], passphrase: String, secondPassphrase: Option[String]): Option[Transaction] = {
    val timeStamp = TimeUtils.getTime()
    val asset = new Asset(Some(votes), None, None)
    val recipientId = CryptoUtils.getAddress(network, CryptoUtils.getKeys(passphrase))
    val signedTransaction = sign(Transaction(None, timeStamp, Some(recipientId), 0, 100000000, VOTE, None, None, None, None, None, Some(asset)), passphrase)
    val finalTransaction = secondPassphrase match {
      case Some(x) => secondSign(signedTransaction.get, x)
      case None => signedTransaction
    }
    finalTransaction.map(e => e.copy(id = Some(CryptoUtils.getId(e))))
  }

  def createDelegate(username: String, passphrase: String, secondPassphrase: Option[String]): Option[Transaction] = {
    val timeStamp = TimeUtils.getTime()
    val asset = new Asset(None, Some(username), None)
    val signedTransaction = sign(Transaction(None, timeStamp, None, 0, 2500000000L, DELEGATE, None, None, None, None, None, Some(asset)), passphrase)
    val finalTransaction = secondPassphrase match {
      case Some(x) => secondSign(signedTransaction.get, x)
      case None => signedTransaction
    }
    finalTransaction.map(e => e.copy(id = Some(CryptoUtils.getId(e))))
  }

  def createSecondSignature(passphrase: String, secondPassphrase: Option[String]): Option[Transaction] = {
    val timeStamp = TimeUtils.getTime()
    val asset = new Asset(None, None, Some(Base16.encode(CryptoUtils.getKeys(secondPassphrase.get).getPubKey)))
    val signedTransaction = sign(Transaction(None, timeStamp, None, 0, 500000000, SECONDSIGNATURE, None, None, None, None, None, Some(asset)), passphrase)
    signedTransaction.map(e => e.copy(id = Some(CryptoUtils.getId(e))))
  }



}