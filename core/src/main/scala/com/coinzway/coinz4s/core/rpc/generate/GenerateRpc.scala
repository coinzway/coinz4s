package com.coinzway.coinz4s.core.rpc.generate

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcJsonFormats
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses._
import com.softwaremill.sttp.SttpBackend

trait GenerateRpc[R[_]] extends BitcoindBaseRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def generate(number: Int)(): R[NodeResponse[HeaderHashes]] =
    client.request[HeaderHashes]("generate", Vector(number))

}
