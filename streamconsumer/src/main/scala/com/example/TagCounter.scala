package com.example


class TagCounter extends CounterActor{
  override def statisticsMessage(counters: Counters):CountersReport = TagCounters(counters)
  override def prepareItems(statuses:Seq[TwitterStatus]): Seq[String] ={
    statuses.flatMap(status => status.hashTags)
  }
}
