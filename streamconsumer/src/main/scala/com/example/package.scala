package com

import akka.actor.ActorRef

package object example {
  case object StreamEnd
  case object Tick
  type Counters = Map[String, Map[String,Int]]

  case object RequestStatistics


  case class TopicClosed(topic: String)

  case class ThroughputSlice(topic: String, count: Int, timestamp: Long )

  case class TwitterStatus(id: Long, status: String, lang: Option[String], isRetweet: Boolean, retweetCount: Int, userId: String, userName: String, userLocation: Option[String], hashTags: Iterable[String])

  case class AggregatedStatus(topic: String, statuses: Seq[TwitterStatus])

  case class AddPipeline(topic: String, receiver: ActorRef)

  case class RemovePipeline(topic: String)

  sealed trait CountersReport{
    val counters:Counters
  }

  case class TagCounters(counters: Counters) extends CountersReport
  case class UserCounters(counters: Counters) extends CountersReport
}
