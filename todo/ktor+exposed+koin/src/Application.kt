package todo

import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.config.ApplicationConfig
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger


fun main(args: Array<String>) {
  embeddedServer(Netty, commandLineEnvironment(args)).start()
}

fun Application.main() {
  database(environment.config)

  transaction {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create( Todos )
    //SchemaUtils.createSequence( TodoService.seq )
  }

  install(DefaultHeaders)
  install(CallLogging)
  install(DataConversion)

  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
    }
  }

  install(Koin) {
    SLF4JLogger()
    modules( module {
      single { TodoService() }
    })
  }

  routing {
    todo()
  }


}

@KtorExperimentalAPI
fun database(hConf: ApplicationConfig) {
  val config = HikariConfig().apply {
    jdbcUrl = hConf.property("ktor.database.url").getString()
    driverClassName = hConf.property("ktor.database.driver").getString()
    username = hConf.property("ktor.database.username").getString()
    password = hConf.property("ktor.database.password").getString()
    isAutoCommit = true
    maximumPoolSize = 3

    //connectionInitSql = "ALTER SESSION SET NSL_DATE_FORMAT='YYYY-MM-DD'"
    addDataSourceProperty("cachePrepStmts", "true")
    addDataSourceProperty("prepStmtCacheSize", "250")
    addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
  }

  val dataSource = HikariDataSource(config)
  Database.connect(dataSource).apply {
    useNestedTransactions = true
  }
}


class ResourceNotFoundException : RuntimeException()



