package com.coinzway.coinz4s.dashd

import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpc
import com.coinzway.coinz4s.core.rpc.estimatesmartfee.EstimateSmartFeeRpc
import com.coinzway.coinz4s.core.rpc.generatetoaddress.GenerateToAddressRpc
import com.coinzway.coinz4s.core.rpc.noWalletbase.NoWalletBaseRpc
import com.softwaremill.sttp.SttpBackend

class DashdClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int
  )(implicit val sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindBaseRpc[R]
    with NoWalletBaseRpc[R]
    with GenerateToAddressRpc[R]
    with EstimateSmartFeeRpc[R] {
  val client = new RpcClient(user, password, host, port, None)
}
