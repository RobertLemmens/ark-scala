package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens
  *
  * This file contains the case classes that map to a json response from the ark network. For convenience purposes, case classes map easily to json.
  */
sealed trait JsonResponse //not sure if or when needed yet.
case class StatusHeader(
                   id: String,
                   height: Long,
                   version: Int,
                   totalAmount: Long,
                   totalFee: Long,
                   reward: Long,
                   payloadHash: String,
                   payloadLength: Int,
                   timestamp: Long,
                   numberOfTransactions: Int,
                   previousBlock: String,
                   generatorPublicKey: String,
                   blockSignature: String)
case class PeerStatus(success: Boolean, height: Long, forgingAllowed: Boolean, currentSlot: Long, header: StatusHeader)
case class PeerResponse(success: Boolean, peers: List[Peer])
case class DelegateResponse(success: Boolean, delegates: List[Delegate], totalCount: Int)
case class TransactionResponse(success: Boolean, transactions: List[Transaction], count: String)
