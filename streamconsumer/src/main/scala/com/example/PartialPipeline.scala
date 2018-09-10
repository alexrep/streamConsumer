package com.example

trait PartialPipeline{
  def close: Unit
  def start: Unit
}