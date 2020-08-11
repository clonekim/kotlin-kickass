package apm.payload

import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotEmpty

data class LoginRequest(
        @field:NotEmpty val username: String,
        @field:NotEmpty val password: String
)


data class ScheduleRequest(
        val timestmp: Long,
        val ind: String?,
        val apo: String,
        val sort: String? = null,
        val scope: String? = null,
        val amId: String
)



@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse(
        val length:Int?,
        val body: Any
)
