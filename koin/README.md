# Koin

https://insert-koin.io/

경량 DI(Dependency Injection) 프레임워크이다  
그외에 kodein이나 dagger가 존재하지만 코인이 간단해서 정리한다

```kt
import org.koin.dsl.module

modules( module {
   single { TodoService() }
})
```

## Ktor

ktor에서 사용 시 SLF4JLogger와 함께 사용하길 권장하며 아래의 의존성이 필요
  - koin-ktor
  - koin-logger-slf4j
    

```kt
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

fun Application.main() {

  install(Koin) {
    SLF4JLogger()
    modules( module {
      single { TodoService() }
    })
  }
  ...
}
```
TodoController.kt 라는 컨트롤러에서 inject를 사용해서 

```kt
fun Route.todo() {
  val service by inject<TodoService>()
  get("/api/todos") {
    call.respond(mapOf("datas" to service.selectAll()))
  }
}
```
