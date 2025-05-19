

import java.time.{LocalDate, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.io.Source

object ProductOrderSystem {

  case class Product(
                      id: String,
                      name: String,
                      price: Double,
                      productType: String,
                      expiryDate: LocalDate
                    )

  case class Order(
                    id: String,
                    product: Product,
                    quantity: Int,
                    timestamp: LocalDateTime,
                    channel: String,
                    paymentMethod: String,
                    discount: Double = 0.0,
                    finalPrice: Double = 0.0
                  )

  def detectProductType(name: String): String = {
    val lowered = name.toLowerCase
    if (lowered.contains("cheese")) "cheese"
    else if (lowered.contains("wine")) "wine"
    else "other"
  }

  def readOrdersFromCSV(filePath: String): List[Order] = {
    val formatterTimestamp = DateTimeFormatter.ISO_DATE_TIME
    val formatterDate = DateTimeFormatter.ISO_LOCAL_DATE

    val lines = Source.fromFile(filePath).getLines().toList.tail

    lines.map { line =>
      val Array(timestampStr, productName, expiryStr, quantityStr, unitPriceStr, channel, paymentMethod) =
        line.split(",").map(_.trim)

      val timestamp = LocalDateTime.parse(timestampStr, formatterTimestamp).atOffset(ZoneOffset.UTC).toLocalDateTime

      val product = Product(
        id = UUID.randomUUID().toString,
        name = productName,
        price = unitPriceStr.toDouble,
        productType = detectProductType(productName),
        expiryDate = LocalDate.parse(expiryStr, formatterDate)
      )

      Order(
        id = UUID.randomUUID().toString,
        product = product,
        quantity = quantityStr.toInt,
        timestamp = timestamp,
        channel =channel ,
        paymentMethod=paymentMethod
      )
    }
  }
}
