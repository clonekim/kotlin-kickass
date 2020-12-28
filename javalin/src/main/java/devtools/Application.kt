package devtools

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path

fun main(args: Array<String>) {

    Javalin.create {
        it.defaultContentType = "application/json"
        it.addStaticFiles("/static")
    }.routes {
        path("/jdbc") {
            get(ApiController::fetchMeta)
        }
    }
    .start(Config.port())



}