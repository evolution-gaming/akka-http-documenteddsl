package akka.http.documenteddsl.util

import java.nio.ByteBuffer
import java.lang.System.currentTimeMillis
import java.util.concurrent.atomic.AtomicInteger

object UID {

  private val atomicCounterFor24 = new AtomicInteger(0)

  def apply(): String = {
    val sixDigitFrom24AtomicCounter   = Math.abs(atomicCounterFor24.incrementAndGet() % 1000000)
    val nanoTimeMicroSecAndTensOfNano = ((System.nanoTime % 1000000) / 10).toInt
    val threeDigitFromThreadId        = Thread.currentThread().getId % 1000

    val arr = ByteBuffer.allocate(12)
      .putLong(currentTimeMillis * 1000000 + sixDigitFrom24AtomicCounter)
      .putInt(nanoTimeMicroSecAndTensOfNano * 100000 + threeDigitFromThreadId.toInt)
      .array

    arr.map("%02X" format _).mkString
  }

}
