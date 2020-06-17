package todo

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


class TodoService {

  /*companion object {

    val seq = Sequence(
      name = "TODO_ID_SEQ",
      startWith = 1,
      incrementBy = 1,
      minValue = 1
    )
  }*/



  suspend fun selectAll(): List<Todo> {
    return transaction {
      Todos.selectAll().map {
        Todo(
          id = it[Todos.id],
          subject = it[Todos.subject],
          content = it[Todos.content],
          done = it[Todos.done],
          createdAt = it[Todos.createdAt]
        )
      }
    }
  }

  suspend fun select(id: Int): Todo? {

    return transaction {
      Todos.select { Todos.id eq id }.single()?.let {
        Todo(
          id = it[Todos.id],
          subject = it[Todos.subject],
          content = it[Todos.content],
          done = it[Todos.done],
          createdAt = it[Todos.createdAt]
        )
      }
    }

  }

  suspend fun create(todo: Todo): Todo {
    val id = transaction {
       Todos.insert {
        it[subject] = todo.subject
        it[content] = todo.content
        it[done] = false
      } get Todos.id

    }

    return todo.copy(id = id)
  }

  suspend fun delete(id: Int): Boolean? {
    return Todos.deleteWhere {  Todos.id eq id }?.let { true }
  }

  suspend fun update(todo: Todo): Boolean? {

    return transaction {
      Todos.update( {Todos.id eq todo.id }) {
        it[content] = todo.content
        it[subject] = todo.subject
        it[done] = todo.done?: false
      }?.let {
        true
      }
    }
  }

}


data class Todo(
  val id: Int,
  val subject: String,
  val content: String,
  val done: Boolean?,
  val createdAt: DateTime?
)

object Todos : Table("todos") {
  val id = integer("id").uniqueIndex("TODO_ID_UNI").autoIncrement("TODO_ID_SEQ")
  val subject = varchar("subject", 255)
  val content = varchar("content", 255)
  val done = bool("done")
  val createdAt = datetime("created_at").clientDefault { DateTime.now() }
}

