import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

fun dbsetup() {
  val config = HikariConfig().apply {
    jdbcUrl = "jdbc:oracle:thin:@//kfosdevdb.koreanair.com:2003/OPERAT"
    driverClassName = "oracle.jdbc.OracleDriver"
    username = "kfos"
    password = "fosk2117"
    isAutoCommit = true
    maximumPoolSize = 3
  }

  val dataSource = HikariDataSource(config)
  Database.connect(dataSource)
}

fun main() {
  dbsetup()

}
