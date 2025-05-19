

//object Main extends App {
//  val filePath = "src/main/resources/TRX1000.csv"
//  val orders = ProductOrderSystem.readOrdersFromCSV(filePath)
//  val processedOrders = DiscountEngine.processOrders(orders)
//
//  processedOrders.foreach { order =>
//    println(s"Product: ${order.product.name}, Quantity: ${order.quantity}, Discount: ${order.discount}%, Final Price: ${order.finalPrice}")
//  }
//}
import java.sql.{Connection, DriverManager, PreparedStatement}

object Main extends App {

  val filePath = "src/main/resources/TRX1000.csv"
  val orders = ProductOrderSystem.readOrdersFromCSV(filePath)
  val processedOrders = DiscountEngine.processOrders(orders)

  val url = "jdbc:postgresql://localhost:5432/postgres"
  val username = "admin"
  val password = "admin"

  var connection: Connection = null

  try {
    connection = DriverManager.getConnection(url, username, password)
    println("Connected to PostgreSQL in Docker container!")

    val createTableSQL =
      """
        |CREATE TABLE IF NOT EXISTS Orders (
        |  Order_Date DATE,
        |  Product_Name TEXT,
        |  Expiry_Date DATE,
        |  Quantity INTEGER,
        |  Unit_Price DOUBLE PRECISION,
        |  Discount DOUBLE PRECISION,
        |  Final_Price DOUBLE PRECISION
        |)
        |""".stripMargin

    val stmt = connection.createStatement()
    stmt.executeUpdate(createTableSQL)
    stmt.close()

    val insertSQL =
      """
        |INSERT INTO Orders (Order_Date, Product_Name, Expiry_Date, Quantity, Unit_Price, Discount, Final_Price)
        |VALUES (?, ?, ?, ?, ?, ?, ?)
        |""".stripMargin

    val pstmt = connection.prepareStatement(insertSQL)

    processedOrders.foreach { order =>
      pstmt.setDate(1, java.sql.Date.valueOf(order.timestamp.toLocalDate))
      pstmt.setString(2, order.product.name)
      pstmt.setDate(3, java.sql.Date.valueOf(order.product.expiryDate))
      pstmt.setInt(4, order.quantity)
      pstmt.setDouble(5, order.product.price)
      pstmt.setDouble(6, order.discount)
      pstmt.setDouble(7, order.finalPrice)
      pstmt.addBatch()
    }

    val insertedCounts = pstmt.executeBatch()
    val totalInserted = insertedCounts.sum
    println(s"Inserted $totalInserted records into Orders table.")

    pstmt.close()

  } catch {
    case e: Exception => e.printStackTrace()
  } finally {
    if (connection != null) connection.close()
  }
}
