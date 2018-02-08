package nl.robertlemmens.core

/**
  * Created by Robert Lemmens on 8-2-18.
  */
sealed trait ErrorCode extends Product with Serializable
case object PeerNotFoundError extends ErrorCode
case object UnexpectedError extends ErrorCode
case object FailureResponse extends ErrorCode
case object HttpError extends ErrorCode