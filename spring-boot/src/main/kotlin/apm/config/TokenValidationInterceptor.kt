package apm.config

import apm.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenValidationInterceptor: HandlerInterceptor {

    private val AUTHORIZATION = "Authorization"
    private val BEARER = "Bearer "

    private val TOKEN_REQUIRED__API = listOf(
            "POST" to "/api/graphql",
            "GET" to "/api/whoami"
    )

    private val ALLOWED_API = listOf(
            "POST" to "/error",
            "POST" to "/api/credential",
            "GET" to "/api/health")


    @Autowired
    lateinit var userService: UserService

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        var pair = request.method to request.servletPath

        return when {
            ALLOWED_API.contains(pair) -> true
            TOKEN_REQUIRED__API.contains(pair) -> {
                val header = request.getHeader(AUTHORIZATION)

                if(!header.isNullOrBlank() && header.startsWith(BEARER)) {
                    val token = header.substring(7)
                    userService.setUser(token)
                    true
                } else {
                    response.sendError(401)
                    false
                }
            }
            else -> {
                response.sendError(401)
                false
            }
        }
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        userService.clear()
    }


}