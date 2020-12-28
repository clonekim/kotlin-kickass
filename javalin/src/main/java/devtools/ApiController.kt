package devtools

import io.javalin.http.Context

object ApiController {


    fun fetchMeta(ctx: Context) {

        JdbcFactory.connection().use { conn ->

            val rs = conn.createStatement().executeQuery("SELECT * FROM users")

            var users = mutableListOf<User>()

            while (rs.next()) {
                users.add(User(rs.getLong("id"), rs.getString("email")))
            }

            ctx.json(users)
        }
    }


    fun executeSQL(ctx: Context) {


    }
}


class User(val id: Long, val email: String)