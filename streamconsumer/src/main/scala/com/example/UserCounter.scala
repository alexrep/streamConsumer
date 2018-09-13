package com.example

class UserCounter extends CounterActor{
  override def statisticsMessage(counters: Counters):CountersReport = UserCounters(counters)
  override def prepareItems(statuses:Seq[TwitterStatus]): Seq[String] ={
    statuses.map(status => status.userId)
  }

}
