package apm

import apm.payload.LoginRequest
import apm.service.User
import apm.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@RestController
class TokenController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/api/credential")
    fun login(@Valid @RequestBody request: LoginRequest): Map<String, String> {
        return mapOf("token" to userService.login(request.username, request.password))
    }

    @GetMapping("/api/whoami")
    fun profile(): User {
        return userService.detail
    }


    @GetMapping("/api/health")
    fun health(): Map<String, String> {
        return mapOf("status" to "ok")
    }

}
