package be.rubenpieters.freeshowcase.util

import be.rubenpieters.freeshowcase.util.json.CirceUtils
import be.rubenpieters.freeshowcase.util.http.HttpUtils

/**
  * Created by ruben on 28/01/17.
  */
trait UtilImplicits
  extends CirceUtils.implicits
    with HttpUtils.implicits

object UtilImplicits extends UtilImplicits
