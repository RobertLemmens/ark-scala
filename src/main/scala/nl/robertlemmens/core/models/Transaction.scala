package nl.robertlemmens.core.models

import java.nio.{ByteBuffer, ByteOrder}

import nl.robertlemmens.core.utils.CryptoUtils
import scorex.crypto.encode.{Base16, Base58}

/**
  *
  * A transaction object is one transaction on the network. This can be a normal transaction, a second signature transaction, delegate transaction or vote transaction.
  * Transactions are made in the TransactionBuilder for convenience. This is also the preferred way.
  *
  */
sealed trait TransactionType
case object NORMAL extends TransactionType
case object SECONDSIGNATURE extends TransactionType
case object CREATEDELEGATE extends TransactionType
case object VOTE extends TransactionType
case object MULTISIGNATURE extends TransactionType

case class Transaction(id: Option[String],
                       timestamp: Long,
                       recipientId: Option[String],
                       amount: Long,
                       fee: Long,
                       transactionType: TransactionType,
                       vendorField: Option[String],
                       requesterPublicKey: Option[String],
                       asset: Option[Asset]) {
  override def toString: String = {
    s"""Transation {
       |id: ${id.getOrElse("None")}
       |timestamp: $timestamp
       |recipientId: ${recipientId.getOrElse("None")}
       |amount: $amount
       |fee: $fee
       |transactionType: $transactionType
       |vendorField: ${vendorField.getOrElse("None")}
       |}
     """.stripMargin
  }
}

object Transaction {

  implicit val TransactionSerializer = {
    def fromBytes = ???
    def toBytes = ???
  }

//  def apply(): Transaction = {
//    Transaction()
//  }

  /**
    * Turn the transaction into an array of bytes
    *
    * @param transaction
    * @return Array[Byte] of this transaction
    */
  def toBytes(transaction: Transaction): Array[Byte] = { //todo cleanup, ugly.
    val buffer = ByteBuffer.allocate(1000)
    buffer.order(ByteOrder.LITTLE_ENDIAN)

    val typeByte = transaction.transactionType match {
      case NORMAL => 0.asInstanceOf[Byte]
      case SECONDSIGNATURE => 1.asInstanceOf[Byte]
      case CREATEDELEGATE => 2.asInstanceOf[Byte]
      case VOTE => 3.asInstanceOf[Byte]
    }

    buffer.put(typeByte)
    buffer.putLong(transaction.timestamp)

    transaction.requesterPublicKey match {
      case Some(x) => buffer.put(Base16.decode(x))
      case None => println("No requester public key")
    }

    transaction.recipientId match {
      case Some(x) => buffer.put(Base58.decode(x).get)
      case None => buffer.put(new Array[Byte](21))
    }

    transaction.vendorField match {
      case Some(x) =>
        val vbytes = x.getBytes()
        if(vbytes.size < 65) {
          buffer.put(vbytes)
          buffer.put(new Array[Byte](64 - vbytes.size))
        }
      case None => buffer.put(Array[Byte](64))
    }

    buffer.putLong(transaction.amount)
    buffer.putLong(transaction.fee)

    transaction.transactionType match {
      case NORMAL => print("Normal")
      case SECONDSIGNATURE => buffer.put(Base16.decode(transaction.asset.get.signature.get))
      case CREATEDELEGATE => buffer.put(transaction.asset.get.username.get.toCharArray.map(_.toByte))
      case VOTE => buffer.put(transaction.asset.get.votes.get.mkString("").toCharArray.map(_.toByte))
    }

    val outBuffer = new Array[Byte](buffer.position())
    buffer.rewind()
    buffer.get(outBuffer)
    outBuffer
  }

  def sign(tx: Transaction, passphrase: String): SignedTransaction = {
    val signedSenderAddress = Base16.encode(CryptoUtils.getKeys(passphrase).getPubKey)
    val signedSignature = Base16.encode(CryptoUtils.sign(tx, passphrase).encodeToDER())

    SignedTransaction(tx, signedSignature, signedSenderAddress, None)
  }

  //todo: how to handle the second sign?
  def secondSign(tx: SignedTransaction, passphrase: String): SignedTransaction = {
        tx.copy(secondSignature = Some(Base16.encode(CryptoUtils.secondSign(tx, passphrase).encodeToDER())))
  }
}

