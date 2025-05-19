

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
        |  Product_Name TEXT,
        |  Quantity INTEGER,
        |  Discount DOUBLE PRECISION,
        |  Final_Price DOUBLE PRECISION
        |)
        |""".stripMargin


    val stmt = connection.createStatement()
    stmt.executeUpdate(createTableSQL)
    stmt.close()

    val insertSQL =
      """
        |INSERT INTO Orders (Product_Name, Quantity, Discount, Final_Price)
        |VALUES (?, ?, ?, ?)
        |""".stripMargin

    val pstmt = connection.prepareStatement(insertSQL)

    processedOrders.foreach { order =>
      pstmt.setString(1, order.product.name)
      pstmt.setInt(2, order.quantity)
      pstmt.setDouble(3, order.discount)
      pstmt.setDouble(4, order.finalPrice)
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
