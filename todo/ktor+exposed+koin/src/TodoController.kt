package todo

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.todo() {

  val service by inject<TodoService>()

  route("/todos") {

    get {
      call.respond(mapOf("datas" to service.selectAll()))
    }

    get("/{id}") {
      val id = call.parameters["id"]!!.toInt()
      val todo = service.select(id) ?: throw ResourceNotFoundException()
      call.respond(todo)
    }

    post {
      val todo = service.create(call.receive())
      call.respond(todo)
    }

    put("/{id}") {
      val todo = call.receive<Todo>()
      call.respond(service.update(todo)?: throw ResourceNotFoundException())
    }

    delete("/{id}") {
      val id = call.parameters["id"]!!.toInt()
      call.respond(service.delete(id)?: throw ResourceNotFoundException())
    }

  }

}
