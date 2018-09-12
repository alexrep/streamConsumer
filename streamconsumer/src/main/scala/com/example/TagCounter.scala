package com.example

import akka.actor.{Actor, ActorLogging}

class TagCounter extends Actor with ActorLogging{
  def receive:Receive = countTags(Map.empty)

  def countTags(counters: Counters): Receive ={
    case AggregatedStatus(topic, statuses) => context.become(countTags(updateCounters(counters, topic, statuses)))
    case TopicClosed(topic) =>  context.become(countTags(counters - topic))
    case RequestStatistics => sender ! TagCounters(counters)
  }

  def updateCounters(counters: Counters, topic :String, statuses : Seq[TwitterStatus]): Counters ={
    if(counters.contains(topic)) {
      counters.updated(topic, updateTopicTags(counters(topic), statuses))
    } else {
      counters + (topic -> initializeTopicTags(statuses))
    }
  }

  def updateTopicTags(tagsCounters: Map[String, Int], statuses: Seq[TwitterStatus]): Map[String, Int] = {
    statuses.flatMap(status => status.hashTags).foldLeft(tagsCounters) { (counters, tag)=>
      if (counters.contains(tag)){
        counters.updated(tag, counters(tag) + 1)
      } else {
        counters + (tag -> 1)
      }
    }
  }

  def initializeTopicTags(statuses: Seq[TwitterStatus]): Map[String, Int] ={
    updateTopicTags(Map.empty, statuses)
  }
}
