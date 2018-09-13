package com.example

trait TweetPropertyCounter {
  this: CounterActor =>
  def updateCounters(counters: Counters, topic :String, statuses : Seq[TwitterStatus]): Counters ={
    if(counters.contains(topic)) {
      counters.updated(topic, updateTopicCounters(counters(topic), statuses))
    } else {
      counters + (topic -> initializeTopicCounters(statuses))
    }
  }

  def updateTopicCounters(tagsCounters: Map[String, Int], statuses: Seq[TwitterStatus]): Map[String, Int] = {
    prepareItems(statuses).foldLeft(tagsCounters) { (counters, tag)=>
      if (counters.contains(tag)){
        counters.updated(tag, counters(tag) + 1)
      } else {
        counters + (tag -> 1)
      }
    }
  }

  def prepareItems(statuses:Seq[TwitterStatus]): Seq[String]

  def initializeTopicCounters(statuses: Seq[TwitterStatus]): Map[String, Int] ={
    updateTopicCounters(Map.empty, statuses)
  }
}
