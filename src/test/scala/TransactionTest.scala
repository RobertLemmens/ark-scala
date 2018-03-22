import nl.robertlemmens.core.TransactionBuilder
import nl.robertlemmens.core.models.{MainNet, Network}
import nl.robertlemmens.core.utils.CryptoUtils
import org.scalatest.FlatSpec
import scorex.crypto.encode.Base16

/**
  * Created by Robert Lemmens on 3-2-18.
  */
class TransactionTest extends FlatSpec{

  "Creating a normal transaction" should "create and verify" in {
    val builder = new TransactionBuilder
    val signedTx = builder.createTransaction(
      "AXoXnFi4z1Z6aFvjEYkDVCtBGW2PaRiM25",
      133380000000L,
      Some("This is the first transaction from SCALA"),
      "this is a top secret passphrase",
      None)

    assert(signedTx.tx.id.isDefined)
    assert(CryptoUtils.verify(signedTx))
  }

  "Creating a normal transaction" should "create and compilation should fail if amount is modified and verification should fail if tx is copied and modified" in {
    val builder = new TransactionBuilder
    val signedTx = builder.createTransaction(
      "AXoXnFi4z1Z6aFvjEYkDVCtBGW2PaRiM25",
      133380000000L,
      Some("This is the first transaction from SCALA"),
      "this is a top secret passphrase",
      None)

    assertDoesNotCompile("tx.get.amount = 10")
    assert(signedTx.tx.id.isDefined)
    val tx2 = signedTx.copy(tx = signedTx.tx.copy(amount = 1333800000000L))
    assert(!CryptoUtils.verify(tx2))
  }

  "Creating a second passphrase: 'second passphrase'" should  "create and verify" in {
    val builder = new TransactionBuilder
    val signedTx = builder.createSecondSignature("this is a top secret passphrase", Some("second passphrase"))
    val secondPublicKey = Base16.encode(CryptoUtils.getKeys("second passphrase").getPubKey)

    assert(signedTx.tx.id.isDefined)
    assert(CryptoUtils.verify(signedTx))
    assert(signedTx.secondSignature.isEmpty)
    assert(secondPublicKey == signedTx.tx.asset.get.signature.get)
    assert(signedTx.tx.asset.get.signature.isDefined)
  }

  "Creating a transaction with a second passphrase" should "create and verify" in {
    val builder = new TransactionBuilder
    val signedTx = builder.createTransaction(
      "AXoXnFi4z1Z6aFvjEYkDVCtBGW2PaRiM25",
      133380000000L,
      Some("This is first transaction from SCALA"),
      "this is a top secret passphrase",
      Some("second passphrase"))

    val secondPublicKey = Base16.encode(CryptoUtils.getKeys("second passphrase").getPubKey)

    assert(signedTx.tx.id.isDefined)
    assert(CryptoUtils.verify(signedTx) && CryptoUtils.secondVerify(signedTx, secondPublicKey))
  }

  "Creating a delegate transaction" should  "create and verify" in {
    val builder = new TransactionBuilder
    val signedTx = builder.createDelegate("eksit", "this is a top secret passphrase", None)

    assert(signedTx.tx.id.isDefined)
    assert(signedTx.tx.asset.get.username.isDefined)
    assert(CryptoUtils.verify(signedTx))
  }

  "Creating a vote transaction" should  "create and verify" in {
    val builder = new TransactionBuilder
    val network = Network(MainNet)
    val signedTx = builder.createVote(network, List("+034151a3ec46b5670a682b0a63394f863587d1bc97483b1b6c70eb58e7f0aed192"),"this is a top secret passphrase", None)

    assert(signedTx.tx.id.isDefined)
    assert(signedTx.tx.asset.get.votes.isDefined)
    assert(CryptoUtils.verify(signedTx))
  }

}
