package devtools

import org.yaml.snakeyaml.Yaml


class Config {

    lateinit var datasource: Datasource
    lateinit var server: Server

    class Datasource(val driverClassName: String,
                     val url: String,
                     val username: String,
                     val password: String)

    class Server(val port: Int)


    companion object {
        private var config: Config? = null

        private fun createInstance(): Config {
            val data = Yaml().run {
                this.javaClass.classLoader.getResourceAsStream("application.yml").use {
                    load(it) as Map<String, Object>
                }
            }

            return Config().apply {
                server = Server((data["server"] as Map<String, Object>)["port"] as Int)
                datasource = (data["datasource"] as Map<String, Object>).run {
                    Datasource(
                            this["driverClassName"] as String,
                            this["url"] as String,
                            this["username"] as String,
                            this["password"] as String
                    )
                }
            }
        }


        private fun instance(): Config {
            return config?: createInstance()
        }

        fun port(): Int {
            return  instance().server.port
        }

        fun datasource() :Datasource {
            return instance().datasource
        }
    }




}