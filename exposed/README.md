# Exposed

https://github.com/JetBrains/Exposed  
[Jetbrains](https://www.jetbrains.com/) 가 만든 경량 ORM이다


## Database 생성

Hikari 데이터소스를 이용해서 데이터베이스를 생성한다  

```kt

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

fun database(hConf: ApplicationConfig) {

  val config = HikariConfig().apply {
    jdbcUrl = hConf.property("ktor.database.url").getString()
    driverClassName = hConf.property("ktor.database.driver").getString()
    username = hConf.property("ktor.database.username").getString()
    password = hConf.property("ktor.database.password").getString()
    isAutoCommit = true
    maximumPoolSize = 3

    connectionInitSql = "ALTER SESSION SET NSL_DATE_FORMAT='YYYY-MM-DD'"
    addDataSourceProperty("cachePrepStmts", "true")
    addDataSourceProperty("prepStmtCacheSize", "250")
    addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
  }

  val dataSource = HikariDataSource(config)
  Database.connect(dataSource).apply {
    useNestedTransactions = true
  }
}


```
## Mapping

```kt 

class TodoService {

  object Todos : Table("todos") {
    val id = integer("id").uniqueIndex("TODO_ID_UNI").autoIncrement("TODO_ID_SEQ")
    val subject = varchar("subject", 255)
    val content = varchar("content", 255)
    val done = char("done", 1)
    val createdAt = datetime("created_at").clientDefault { DateTime.now() }
  }

}

```

시간을 다루기 위해선 필요한 의존성에 주의 해야한다  
1. exposed-jodatime
1. exposed-java-time (JDK8)  
둘 중 하나를 선별해야 한다.
실제 ORM이 작성하는 쿼리를 보면 문자열로 시간컬럼에 넣기 때문에  
ALTER SESSION ALTER SESSION SET NSL_DATE_FORMAT 으로 시간포맷을 맞춰야 할 필요가 있다  
그렇치 않고 TO_DATE로 감싸야 하는 SQL를 생성하고 싶다면 exposed의 확장하면 된다.


autoIncrement 를 사용 시 오라클인 경우 시퀀스를 만들어준다.  
만약 uniqueIndex로만 사용하고 직접 시퀀스를 다뤄야 직접 작성 할 수 있다.  

```kt 
    companion object {

    val seq = Sequence(
      name = "TODO_ID_SEQ",
      startWith = 1,
      incrementBy = 1,
      minValue = 1
    )
  }

```


어플리케이션 초기화 시 SchemaUtils를 이용해서 테이블과 관련 오브젝트를 생성할 수 있다  

```kt 

 transaction {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create( Todos )
    //SchemaUtils.createSequence( TodoService.seq )  직접 시퀀스를 작성 할 경우
  }

```

## CRUD

```kt 
data class Todo(
  val id: Int,
  val subject: String,
  val content: String,
  val done: Boolean?,
  val createdAt: DateTime?
)
```

모든 쿼리의 실행은 반드시 transaction 블럭안에서 실행되어진다  
TodoService.kt*

```kt 
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


class TodoService {

  fun selectAll(): List<Todo> {
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

  fun select(id: Int): Todo? {

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

  fun create(todo: Todo): Todo {
    val id = transaction {

       Todos.insert {
       // it[id] = seq.nextVal() 직접 시퀀스를 호출 할 경우
        it[subject] = todo.subject
        it[content] = todo.content
        it[done] = todo.done
      } get Todos.id

    }

    return todo.copy(id = id)
  }

  fun delete(id: Int): Boolean? {
    return Todos.deleteWhere {  Todos.id eq id }?.let { true }
  }

  fun update(todo: Todo): Boolean? {

    return transaction {
      Todos.update( {Todos.id eq todo.id }) {
        it[content] = todo.content
        it[subject] = todo.subject
        it[done] = todo.done
      }?.let {
        true
      }
    }
  }

}

```

