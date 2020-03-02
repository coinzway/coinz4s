package com.coinzway.coinz4s.zcashd

import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpc
import com.coinzway.coinz4s.core.rpc.estimatesmartfee.EstimateSmartFeeRpc
import com.coinzway.coinz4s.core.rpc.generate.GenerateRpc
import com.coinzway.coinz4s.core.rpc.noWalletbase.NoWalletBaseRpc
import com.softwaremill.sttp.SttpBackend

class ZcashdCashClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int
  )(implicit val sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindBaseRpc[R]
    with EstimateSmartFeeRpc[R]
    with NoWalletBaseRpc[R]
    with GenerateRpc[R] {
  val client = new RpcClient(user, password, host, port, None)
}
