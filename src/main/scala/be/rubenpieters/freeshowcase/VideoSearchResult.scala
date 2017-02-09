package be.rubenpieters.freeshowcase

import java.text.Normalizer
import java.util.Locale

import cats.data.NonEmptyList
import cats.implicits._
import org.simmetrics.builders.StringMetricBuilder
import org.simmetrics.metrics.JaroWinkler
import org.simmetrics.simplifiers.{Simplifier, Simplifiers}
import org.simmetrics.tokenizers.Tokenizers
import be.rubenpieters.freeshowcase.Searchable.ops._

/**
  * Created by ruben on 5/01/17.
  */
case class VideoSearchResult(results: List[Video])

object VideoSearchResult {
  import Video._

  def reorderResults[A: Searchable](a: A, videoSearchResult: VideoSearchResult): VideoSearchResult = {
    VideoSearchResult(
      videoSearchResult.results
      .map(t => (t, metric.compare(t.title, a.asSearchLiteral)))
      .filter{ case (t, m) => m >= similarityThreshold}
      .sortBy(- _._2)
      .map(_._1)
    )
  }

  def mostRelevantResult[A: Searchable](a: A, videoSearchResult: VideoSearchResult): Option[Video] =
    reorderResults(a, videoSearchResult).results.headOption
}

case class Video(title: VideoTitle, url: VideoUrl, videoId: VideoId)

object Video {
  val similarityThreshold = 0.8f

  val accentNormalizer = new Simplifier {
    override def simplify(input: String): String = normalize(input)
  }

  val metric =
    StringMetricBuilder.`with`(new JaroWinkler())
      .simplify(Simplifiers.toLowerCase(Locale.ENGLISH))
      .simplify(accentNormalizer)
      .build()

  def containsTermsFully(title: VideoTitle, terms: SearchTerms): Boolean = {
    ! terms.exists(term => ! title.contains(term))
  }

  def normalize(x: String) = Normalizer.normalize(x, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
}