package com.coinzway.coinz4s.bitcoindCash

import com.coinzway.coinz4s.bitcoind.BitcoindClient
import com.softwaremill.sttp.SttpBackend

class BitcoindCashClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int,
    wallet: Option[String] = None
  )(implicit sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindClient(user, password, host, port, wallet) {}
