package apm.service

import apm.config.JwtProvider
import apm.exception.BadCredentialException
import apm.exception.ResourceNotFoundException
import apm.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

@Component
@Validated
class UserService {

    private val userContext = ThreadLocal.withInitial{ User() }


    @Autowired
    lateinit var userMapper: UserMapper

    @Autowired
    lateinit var hashSigner: HashSigner

    @Autowired
    lateinit var jwtProvider: JwtProvider

    val amId: String? get() = userContext.get().amId
    val name: String? get() = userContext.get().name
    val nick: String? get() = userContext.get().nick
    val detail: User get() = userContext.get()

    fun setUser(token: String) {
        val user =  jwtProvider.encodeJWT(token)
        userContext.set(user)
    }

    fun clear() {
        userContext.remove()
    }


    fun login(@NotEmpty username: String, @NotEmpty  password: String): String {
        return userMapper.selectLogin(username)?.let {

            if(hashSigner.compare(it.encPasswd, password)) {
                //userMapper.updateLoginCount(username)
                jwtProvider.createJWT(User(it.korN, it.engN, it.empNo))
            }else {
                throw BadCredentialException("비밀번호가 일치하지 않습니다")
            }

        } ?: throw ResourceNotFoundException()
    }


}