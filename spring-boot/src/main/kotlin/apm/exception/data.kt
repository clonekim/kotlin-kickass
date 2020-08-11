package apm.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException : GraphQLRuntimeError {
    constructor() : super()
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, t: Throwable?) : super(msg, t)

    override fun getStatus(): Int {
        return HttpStatus.NOT_FOUND.value()
    }
}


@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadCredentialException : GraphQLRuntimeError {
    constructor() : super()
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, t: Throwable?) : super(msg, t)

    override fun getStatus(): Int {
        return HttpStatus.BAD_REQUEST.value()
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationFailException : GraphQLRuntimeError {
    constructor() : super()
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, t: Throwable?) : super(msg, t)

    override fun getStatus(): Int {
        return HttpStatus.BAD_REQUEST.value()
    }
}