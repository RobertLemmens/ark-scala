package nl.robertlemmens.core.models

import java.nio.{ByteBuffer, ByteOrder}

import scorex.crypto.encode.{Base16, Base58}

/**
  * Created by Robert Lemmens on 1-2-18.
  */
sealed trait TransactionType
case object NORMAL extends TransactionType
case object SECONDSIGNATURE extends TransactionType
case object DELEGATE extends TransactionType
case object VOTE extends TransactionType

case class Transaction(id: Option[String],
                       timestamp: Long,
                       recipientId: Option[String],
                       amount: Long,
                       fee: Long,
                       transactionType: TransactionType,
                       vendorField: Option[String],
                       signature: Option[String],
                       signSignature: Option[String],
                       senderPublicKey: Option[String],
                       requesterPublicKey: Option[String],
                       asset: Option[Asset])

object Transaction {
  def toBytes(transaction: Transaction, skipSignature: Boolean, skipSecondSignature: Boolean): Array[Byte] = {
    val buffer = ByteBuffer.allocate(1000)
    buffer.order(ByteOrder.LITTLE_ENDIAN)

    val typeByte = transaction.transactionType match {
      case NORMAL => 0.asInstanceOf[Byte]
      case SECONDSIGNATURE => 1.asInstanceOf[Byte]
      case DELEGATE => 2.asInstanceOf[Byte]
      case VOTE => 3.asInstanceOf[Byte]
    }

    buffer.put(typeByte)
    buffer.putLong(transaction.timestamp)
    buffer.put(Base16.decode(transaction.senderPublicKey.get))

    transaction.requesterPublicKey match {
      case Some(x) => buffer.put(Base16.decode(x))
      case None => println("No requester public key key")
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
      case NORMAL => println("Normal transaction")
      case SECONDSIGNATURE => buffer.put(Base16.decode(transaction.asset.get.signature.get))
      case DELEGATE => buffer.put(transaction.asset.get.username.get.toCharArray.map(_.toByte))
      case VOTE => buffer.put(transaction.asset.get.votes.get.mkString("").toCharArray.map(_.toByte))
    }

    if(!skipSignature) {
      transaction.signature match {
        case Some(x) => buffer.put(Base16.decode(x))
        case None => println("No signature")
      }
    }

    if(!skipSecondSignature) {
      transaction.signSignature match {
        case Some(x) => buffer.put(Base16.decode(x))
        case None => println("No second signature")
      }
    }

    val outBuffer = new Array[Byte](buffer.position())
    buffer.rewind()
    buffer.get(outBuffer)
    outBuffer
  }
}

