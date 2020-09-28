package com.coinzway.coinz4s.core

object BaseResponses {
  type NodeResponse[T] = Either[ErrorResponse, T]

  trait ErrorResponse {
    def errorMessage: String
  }

  final case class GeneralErrorResponse(errorMessage: String) extends ErrorResponse

}
