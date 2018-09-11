package com

import akka.actor.ActorRef

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
                            hashTags: Iterable[String]
                          )

  case class AggregatedStatus(topic: String, statuses: Seq[TwitterStatus])

  case class AddPipeline(topic: String, receiver: ActorRef)

  case class RemovePipeline(topic: String)
}
