package com.coinzway.coinz4s.core.rpc

import com.coinzway.coinz4s.core.BaseResponses.{GeneralErrorResponse, NodeResponse}
import com.softwaremill.sttp._
import spray.json._

import scala.util.{Failure, Success, Try}

class RpcClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int,
    wallet: Option[String]
  )(implicit sttpBackend: SttpBackend[R, Nothing]) {
  implicit private val monadError: MonadError[R] = sttpBackend.responseMonad
  private val walletPath = wallet.map(w => s"/wallet/$w").getOrElse("")
  private val uri = s"http://$host:$port$walletPath"

  def request[T](methodName: String, params: Vector[Any])(implicit jsonReader: JsonReader[T]): R[NodeResponse[T]] = {
    import com.softwaremill.sttp.monadSyntax._
    sttp.auth
      .basic(user, password)
      .post(Uri.parse(uri).getOrElse(throw new RuntimeException(s"invalid wallet connection url: $uri")))
      .body(method(methodName, params))
      .response(as[T])
      .send()
      .map(response => response.body.left.map(error => GeneralErrorResponse(error)).joinRight)

  }

  private def as[T](implicit reader: JsonReader[T]): ResponseAs[NodeResponse[T], Nothing] =
    asString.map { r =>
      val responseObject = r.parseJson.asJsObject
      responseObject.fields("result") match {
        case JsNull =>
          Left(GeneralErrorResponse(responseObject.fields.get("error").map(_.toString).getOrElse("Unknown error")))
        case json: JsValue =>
          Try(json.convertTo[T]) match {
            case Success(success) => Right(success)
            case Failure(_)       => Left(GeneralErrorResponse(s"Error parsing JSON, got: ${json.compactPrint}"))
          }
      }
    }

  private def method(methodName: String, params: Vector[Any]): String =
    if (params.isEmpty) {
      s"""{"method": "$methodName"}"""
    } else {
      val formattedParams = HttpParamsConverter.rpcParamsToJson(params)
      s"""{"method": "$methodName", "params": [${formattedParams.mkString(",")}]}"""
    }

}
