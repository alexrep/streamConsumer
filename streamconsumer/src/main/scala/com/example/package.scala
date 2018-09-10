package com

package object example {
  case object StreamEnd
  case object Tick

  case class TopicClosed(topic: String)

  case class ThroughputSlice(topic: String, count: Int, timestamp: Long )

  case class TwitterStatus(
                            status: String,
                            lang: Option[String],
                            isRetweet: Boolean,
                            retweetCount: Int,
                            userId: Long,
                            userName: String,
                            userLocation: Option[String],
                            hashTags: Seq[String]
                          )

  case class AggregatedStatus(topic: String, statuses: Seq[TwitterStatus])
}
