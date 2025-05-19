

import ProductOrderSystem.Order

object DiscountEngine {
  type DiscountRule = (Order => Boolean, Order => Double)

  def processOrders(orders: List[Order]): List[Order] = {
    val rules = getDiscountRules()
    orders.map(order => applyDiscounts(order, rules))
  }

  private def getDiscountRules(): List[DiscountRule] = List(
    (isExpiringSoon, calculateExpiryDiscount),
    (isCheeseOrWine, calculateProductTypeDiscount),
    (isMarch23rd, _ => 50.0),
    (isBulkPurchase, calculateBulkDiscount),
    (isAppChannel, calculateAppChannelDiscount),
    (isVisaPayment, _ => 5.0)
  )

  private def isExpiringSoon(order: Order): Boolean = {
    val daysRemaining = order.product.expiryDate.toEpochDay - order.timestamp.toLocalDate.toEpochDay
    daysRemaining < 30
  }

  private def calculateExpiryDiscount(order: Order): Double = {
    val daysRemaining = order.product.expiryDate.toEpochDay - order.timestamp.toLocalDate.toEpochDay
    30 - daysRemaining
  }

  private def isCheeseOrWine(order: Order): Boolean = {
    order.product.productType match {
      case "cheese" | "wine" => true
      case _ => false
    }
  }

  private def calculateProductTypeDiscount(order: Order): Double = {
    order.product.productType match {
      case "cheese" => 10.0
      case "wine" => 5.0
      case _ => 0.0
    }
  }

  private def isMarch23rd(order: Order): Boolean = {
    order.timestamp.getMonthValue == 3 && order.timestamp.getDayOfMonth == 23
  }

  private def isBulkPurchase(order: Order): Boolean = {
    order.quantity > 5
  }

  private def calculateBulkDiscount(order: Order): Double = {
    order.quantity match {
      case q if q >= 15 => 10.0
      case q if q >= 10 => 7.0
      case q if q >= 6  => 5.0
      case _ => 0.0
    }
  }

  private def isAppChannel(order: Order): Boolean = {
    order.channel.equalsIgnoreCase("App")
  }

  private def calculateAppChannelDiscount(order: Order): Double = {
    val quantity = order.quantity
    if (quantity <= 5) 5.0
    else if (quantity <= 10) 10.0
    else 15.0
  }


  private def isVisaPayment(order: Order): Boolean = {
    order.paymentMethod.equalsIgnoreCase("Visa")
  }


  private def applyDiscounts(order: Order, rules: List[DiscountRule]): Order = {
    val applicable = rules.collect {
      case (cond, calc) if cond(order) => calc(order)
    }.sorted(Ordering[Double].reverse).take(2)

    val avgDiscount = if (applicable.isEmpty) 0.0 else applicable.sum / applicable.length
    val finalPrice = order.product.price * (1 - avgDiscount / 100)

    order.copy(discount = avgDiscount, finalPrice = finalPrice)
  }
}
