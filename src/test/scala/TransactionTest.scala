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
    val tx = builder.createTransaction(
      "AXoXnFi4z1Z6aFvjEYkDVCtBGW2PaRiM25",
      133380000000L,
      Some("This is the first transaction from SCALA"),
      "this is a top secret passphrase",
      None)

    assert(tx.isDefined)
    assert(CryptoUtils.verify(tx.get))
  }

  "Creating a normal transaction" should "create and compilation should fail if amount is modified and verification should fail if tx is copied and modified" in {
    val builder = new TransactionBuilder
    val tx = builder.createTransaction(
      "AXoXnFi4z1Z6aFvjEYkDVCtBGW2PaRiM25",
      133380000000L,
      Some("This is the first transaction from SCALA"),
      "this is a top secret passphrase",
      None)

    assertDoesNotCompile("tx.get.amount = 10")
    assert(tx.isDefined)
    val tx2 = tx.map(_.copy(amount = 1333800000000L))
    assert(!CryptoUtils.verify(tx2.get))
  }

  "Creating a second passphrase: 'second passphrase'" should  "create and verify" in {
    val builder = new TransactionBuilder
    val tx = builder.createSecondSignature("this is a top secret passphrase", Some("second passphrase"))
    val secondPublicKey = Base16.encode(CryptoUtils.getKeys("second passphrase").getPubKey)

    assert(tx.isDefined)
    assert(CryptoUtils.verify(tx.get))
    assert(tx.get.signSignature.isEmpty)
    assert(secondPublicKey == tx.get.asset.get.signature.get)
    assert(tx.get.asset.get.signature.isDefined)
  }

  "Creating a transaction with a second passphrase" should "create and verify" in {
    val builder = new TransactionBuilder
    val tx = builder.createTransaction(
      "AXoXnFi4z1Z6aFvjEYkDVCtBGW2PaRiM25",
      133380000000L,
      Some("This is first transaction from SCALA"),
      "this is a top secret passphrase",
      Some("second passphrase"))

    println(tx.get)

    val secondPublicKey = Base16.encode(CryptoUtils.getKeys("second passphrase").getPubKey)

    assert(tx.isDefined)
    assert(CryptoUtils.verify(tx.get) && CryptoUtils.secondVerify(tx.get, secondPublicKey))
  }

  "Creating a delegate transaction" should  "create and verify" in {
    val builder = new TransactionBuilder
    val tx = builder.createDelegate("eksit", "this is a top secret passphrase", None)

    assert(tx.isDefined)
    assert(tx.get.asset.get.username.isDefined)
    assert(CryptoUtils.verify(tx.get))
  }

  "Creating a vote transaction" should  "create and verify" in {
    val builder = new TransactionBuilder
    val network = Network(MainNet)
    val tx = builder.createVote(network, List("+034151a3ec46b5670a682b0a63394f863587d1bc97483b1b6c70eb58e7f0aed192"),"this is a top secret passphrase", None)

    assert(tx.isDefined)
    assert(tx.get.asset.get.votes.isDefined)
    assert(CryptoUtils.verify(tx.get))
  }

}
