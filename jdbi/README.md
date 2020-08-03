```kotlin


object HelloJdbi {

    val jdbi: Jdbi

    init {
        ApplicationConfig("application.yml")
        val dataSource = HikariConfig().let { config ->
            config.driverClassName = database.driverClassName
            config.jdbcUrl = database.url
            config.username = database.username
            config.password = database.password
            config.maximumPoolSize = 4
            HikariDataSource(config)
        }


        jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(KotlinPlugin())
    }


}

fun dateHandleTest() {
    val date = HelloJdbi.jdbi.withHandle<Date, SQLException> { handle ->
        handle.createQuery("SELECT SYSDATE FROM DUAL")
            .map { rs, _ -> Date(rs.getTimestamp(1).time) }
            .first()
    }

    println("Date -> $date")
}


fun complexSQL() {
    val sql = """
    SELECT ? AS SEQ,        
       PROCESS_IND,
       SYSDATE,
       MANUAL_NM,
       BLUE_Y
    FROM CHKLIST WHERE DEP_ARR_IND = (SELECT DEP_ARR_IND FROM OR_SKD_T WHERE ORSKD_SEQ_NR = ?)  AND SYSDATE BETWEEN EFF_DT AND EXP_DT
    """.trimIndent()

    val list = HelloJdbi.jdbi.withHandle<List<Map<String, Any>>, SQLException> { handle ->
        handle.createQuery(sql)
            .bind(0, 751671)
            .bind(1, 751671)
            .mapToMap()
            .list().toMutableList()
    }

    println(list)
}


```
