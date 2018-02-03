package nl.robertlemmens.core.models

/**
  * Created by Robert Lemmens on 1-2-18.
  */
sealed trait NetworkType
case object MainNet extends NetworkType
case object DevNet extends NetworkType

case class Network(
                    netHash: String,
                    name: String,
                    port: Int,
                    prefix: Int,
                    version: String,
                    broadCastMax: Int,
                    peerSeed: List[String],
                    peers: List[Peer],
                    peerListProviders: List[String])

object Network{
  def apply(net: NetworkType): Network = {
    net match {
      case MainNet => {
        new Network(
          "6e84d08bd299ed97c212c886c98a57e36545c8f5d645ca7eeae63a8bd62d8988",
          "mainnet",
          4001,
          0x17,
          "1.0.1",
          10,
          List(
            "5.39.9.240:4001",
            "5.39.9.241:4001",
            "5.39.9.242:4001",
            "5.39.9.243:4001",
            "5.39.9.244:4001",
            "5.39.9.245:4001",
            "5.39.9.246:4001",
            "5.39.9.247:4001",
            "5.39.9.248:4001",
            "5.39.9.249:4001",
            "5.39.9.250:4001",
            "5.39.9.251:4001",
            "5.39.9.252:4001",
            "5.39.9.253:4001",
            "5.39.9.254:4001",
            "5.39.9.255:4001"),
          List(),
          List(
            "https://node1.arknet.cloud",
            "https://node2.arknet.cloud"))
      }
      case DevNet =>{
        new Network(
          "578e820911f24e039733b45e4882b73e301f813a0d2c31330dafda84534ffa23",
          "devnet",
          4002,
          0x1e,
          "1.0.1",
          10,
          List(
            "167.114.29.52:4002",
            "167.114.29.53:4002",
            "167.114.29.55:4002"),
          List(),
          List())
      }
    }
  }
}