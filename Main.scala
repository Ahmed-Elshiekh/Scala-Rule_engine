

object Main extends App {
  val filePath = "src/main/resources/TRX1000.csv"
  val orders = ProductOrderSystem.readOrdersFromCSV(filePath)
  val processedOrders = DiscountEngine.processOrders(orders)

  processedOrders.foreach { order =>
    println(s"Product: ${order.product.name}, Quantity: ${order.quantity}, Discount: ${order.discount}%, Final Price: ${order.finalPrice}")
  }
}
