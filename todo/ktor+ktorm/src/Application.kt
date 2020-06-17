package todo

import com.google.gson.annotations.SerializedName
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.config.ApplicationConfig
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.gson.gson
import io.ktor.http.content.default
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.schema.*
import org.slf4j.event.Level
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        gson()
    }

    install(DataConversion){

    }

    val database = dbsetup(environment.config)


    routing {

        get("/api/todos") {
            call.respond(
                database
                    .from(TodoTable)
                    .select()
                    .map { row ->
                        Todo(
                            id = row[TodoTable.id],
                            subject = row[TodoTable.subject],
                            content = row[TodoTable.content],
                            createdAt = row[TodoTable.createdAt]
                        )
                    })
        }

        get("/api/todos/{id}") {
            var id = call.parameters["id"]!!.toInt()
            call.respond(
                database
                    .from(TodoTable)
                    .select()
                    .where { TodoTable.id eq id }
                    .map { row ->
                        Todo(
                            id = row[TodoTable.id],
                            subject = row[TodoTable.subject],
                            content = row[TodoTable.content],
                            createdAt = row[TodoTable.createdAt]
                        )
                    }
            )
        }


        post("/api/todos") {

            val todo = call.receive<Todo>()
            log.debug("todo insert -> {}", todo)
            val now = Date()
            val id = database
                .insert(TodoTable) {
                    it.subject to todo.subject
                    it.content to todo.content
                    it.createdAt to now
                }

            call.respond(todo.copy(id = id, createdAt = now))

        }

        delete("/api/todos/{id}") {
            val id = call.parameters["id"]!!.toInt()

            call.respond( database.delete(TodoTable) {
                it.id eq id
            })
        }


        resource("/*", "static/index.html")
        static("/") {
            resources("static")
            default("static/index.html")
        }
    }
}

data class Todo(
    val id: Int?,
    val subject: String?,
    val content: String?,
    @SerializedName("created_at")
    val createdAt: Date?
)

object TodoTable: Table<Nothing> ("todos") {
    val id by int("id").primaryKey()
    val subject by varchar("subject")
    val content by text("content")
    val createdAt by jdbcDate("created_at")
}



fun dbsetup(hConf: ApplicationConfig): Database {
    val config = HikariConfig()
    config.jdbcUrl = hConf.property("ktor.jdbc.url").getString()
    config.driverClassName = hConf.property("ktor.jdbc.driver").getString()
    config.username = hConf.property("ktor.jdbc.username").getString()
    config.password = hConf.property("ktor.jdbc.password").getString()
    config.isAutoCommit = true
    config.maximumPoolSize = 3

    val ds = HikariDataSource(config)
    return Database.connect(ds)
}
