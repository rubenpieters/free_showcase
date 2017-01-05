package be.rubenpieters.freeshowcase

import cats.data.NonEmptyList

/**
  * Created by ruben on 5/01/17.
  */
case class VideoSearchResult(results: List[Video])

case class Video(title: String, url: String)