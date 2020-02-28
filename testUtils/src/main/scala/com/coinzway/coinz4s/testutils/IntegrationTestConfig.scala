package com.coinzway.coinz4s.testutils

import com.coinzway.coinz4s.core.{Conf, WalletConnectionParams}
import pureconfig._
import pureconfig.generic.auto._

trait IntegrationTestConfig {
  val conf: WalletConnectionParams =
    ConfigSource.default.load[Conf] match {
      case Left(value) => throw new RuntimeException(s"Error reading config: $value")
      case Right(value) =>value.wallet
    }
}
