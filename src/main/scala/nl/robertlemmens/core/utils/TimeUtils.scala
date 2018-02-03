package nl.robertlemmens.core.utils

import java.time.{Duration, Instant}

/**
  * Created by Robert Lemmens on 2-2-18.
  */
object TimeUtils {

  val epochDate = Instant.parse("2017-03-21T13:00:00Z")

  def getTime(timeStamp: Instant): Long = Duration.between(timeStamp, epochDate).toMillis

  def getTime(): Long = Duration.between(epochDate, Instant.now()).toMillis


}
