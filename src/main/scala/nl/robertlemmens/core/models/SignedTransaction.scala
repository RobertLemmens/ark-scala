package nl.robertlemmens.core.models

import java.nio.{ByteBuffer, ByteOrder}

import scorex.crypto.encode.{Base16, Base58}

/**
  * A signed transaction data type
  */
case class SignedTransaction(tx: Transaction, signature: String, senderPublicKey: String, secondSignature: Option[String]) { //optional second signature?
  override def toString: String = {
    s"""SignedTransaction {
       |Transaction: $tx
       |Signature: $signature
       |Senderaddress: $senderPublicKey
       |SecondSignature: $secondSignature
       |}
     """.stripMargin
  }
}
//todo use this for second signed transactions aswell, or create a new data type?
object SignedTransaction {
  /*
      Todo; cleanup. Id like to just combine tx.toBytes with the signature bytes instead of use the code twice like this
   */
  def toBytes(signedTransaction: SignedTransaction, skipSecondSignature: Boolean): Array[Byte] = {
    val buffer = ByteBuffer.allocate(1000)
    buffer.order(ByteOrder.LITTLE_ENDIAN)

    val transaction = signedTransaction.tx

    val typeByte = transaction.transactionType match {
      case NORMAL => 0.asInstanceOf[Byte]
      case SECONDSIGNATURE => 1.asInstanceOf[Byte]
      case CREATEDELEGATE => 2.asInstanceOf[Byte]
      case VOTE => 3.asInstanceOf[Byte]
    }

    buffer.put(typeByte)
    buffer.putLong(transaction.timestamp)
    buffer.put(Base16.decode(signedTransaction.senderPublicKey))

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

    signedTransaction.tx.transactionType match {
      case NORMAL => print("Normal")
      case SECONDSIGNATURE => buffer.put(Base16.decode(transaction.asset.get.signature.get))
      case CREATEDELEGATE => buffer.put(transaction.asset.get.username.get.toCharArray.map(_.toByte))
      case VOTE => buffer.put(transaction.asset.get.votes.get.mkString("").toCharArray.map(_.toByte))
    }

    buffer.put(Base16.decode(signedTransaction.signature)) //does a signed signature ever NOT have a signature to put in buffer?
    if(!skipSecondSignature) {
      signedTransaction.secondSignature match {
        case Some(x) => buffer.put(Base16.decode(x))
        case None => println("No second signature") //how to handle this None case
      }
    }

    val outBuffer = new Array[Byte](buffer.position())
    buffer.rewind()
    buffer.get(outBuffer)
    outBuffer
  }

}
