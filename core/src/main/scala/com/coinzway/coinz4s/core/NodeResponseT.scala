package com.coinzway.coinz4s.core

import BaseResponses._
import com.softwaremill.sttp.MonadError

final case class NodeResponseT[R[_], A <: CorrectResponse](
    value: R[NodeResponse[A]]
  )(implicit monadError: MonadError[R]) {

  import com.softwaremill.sttp.monadSyntax._

  def map[B <: CorrectResponse](f: A => B): NodeResponseT[R, B] =
    NodeResponseT(value.map(_.map(f)))

  def flatMap[B <: CorrectResponse](f: A => NodeResponseT[R, B]): NodeResponseT[R, B] =
    NodeResponseT(value.flatMap {
      case Right(a)    => f(a).value
      case Left(error) => monadError.unit(Left(error))
    })

}