package com.coinzway.coinz4s.core.rpc.estimatesmartfee

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.ClientObjects.EstimateMode
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.estimatesmartfee.EstimateSmartFeeResponse.EstimateSmartFee
import com.softwaremill.sttp.SttpBackend

trait EstimateSmartFeeRpc[R[_]] extends EstimateSmartFeeRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def estimateSmartFee(
      confTarget: Int,
      estimateMode: Option[EstimateMode.Value] = None
    )(
    ): R[NodeResponse[EstimateSmartFee]] =
    client.request[EstimateSmartFee]("estimatesmartfee", confTarget +: estimateMode.map(_.toString).toVector)

}
