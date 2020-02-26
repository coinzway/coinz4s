package com.coinzway.coinz4s.zcashd

import com.coinzway.coinz4s.bitcoind.BitcoindClient
import com.softwaremill.sttp.SttpBackend

class ZcashdCashClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int
  )(implicit sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindClient(user, password, host, port, None) {}
