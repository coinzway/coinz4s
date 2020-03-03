package com.coinzway.coinz4s.core.rpc.estimatefee

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.estimatefee.EstimateFeeResponse.EstimateFee
import com.softwaremill.sttp.SttpBackend

trait EstimateFeeRpc[R[_]] extends EstimateFeeRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def estimateFee()(): R[NodeResponse[EstimateFee]] =
    client.request[EstimateFee]("estimatefee", Vector.empty)

}
