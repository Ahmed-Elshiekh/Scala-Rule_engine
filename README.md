"#Scala-Rule_engine" 
Overview

This Scala project processes product orders imported from a CSV file, applies multiple discount rules, and calculates the final price for each order. It simulates a simple discount engine considering product type, expiry date, purchase channel, payment method, and quantity.

Features
Reads orders from a CSV file with fields:
timestamp, product name, expiry date, quantity, unit price, channel, payment method
Identifies product types based on product names ( cheese, wine, others)

Applies multiple discount rules including:

Expiry date nearing discount

Product-type-specific discounts

Special date discount (March 23rd)

Bulk purchase discounts

Discounts based on purchase channel (App)

Discounts for Visa payment method

Calculates average of top two applicable discounts per order

Outputs orders enriched with discount and final price information

Case Classes

Product: Represents a product with fields like id, name, price, productType, and expiryDate.

Order: Represents a customer's order with product info, quantity, timestamp, channel, payment method, discount, and final price.



src/
 └── main/
      └── resources/
           └── TRX1000.csv        
 └── scala/
      ├── ProductOrderSystem.scala  
      └── DiscountEngine.scala      
      └── Main.scala                    
