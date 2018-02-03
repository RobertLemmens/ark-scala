package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens on 3-2-18.
  */
sealed trait JsonResponse
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
