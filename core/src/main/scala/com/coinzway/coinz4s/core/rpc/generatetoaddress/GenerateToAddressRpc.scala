package com.coinzway.coinz4s.core.rpc.generatetoaddress

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcJsonFormats
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses._
import com.softwaremill.sttp.SttpBackend

trait GenerateToAddressRpc[R[_]] extends BitcoindBaseRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def generatetoaddress(number: Int, address: String)(): R[NodeResponse[HeaderHashes]] =
    client.request[HeaderHashes]("generatetoaddress", Vector(number, address))

}
