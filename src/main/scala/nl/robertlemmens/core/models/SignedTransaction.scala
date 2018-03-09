package nl.robertlemmens.core.models

/**
  * A signed transaction data type
  */
case class SignedTransaction(tx: Transaction, signature: String, senderAddress: String) {
  override def toString: String = {
    s"""SignedTransaction {
       |Transaction: $tx
       |Signature: $signature
       |Senderaddress: $senderAddress
       |}
     """.stripMargin
  }
}
 //todo use this for second signed transactions aswell, or create a new data type?
object SignedTransaction {

}
