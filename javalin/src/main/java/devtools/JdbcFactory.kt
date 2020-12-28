package devtools

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

class JdbcFactory {


    companion object {

        val log: Logger = LoggerFactory.getLogger(this.javaClass)

        fun connection(): Connection {
            log.debug("create connection")
            return DriverManager
                    .getConnection(Config.datasource().url, Config.datasource().username, Config.datasource().password )

        }
    }
}