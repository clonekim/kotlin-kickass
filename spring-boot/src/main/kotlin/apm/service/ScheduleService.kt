package apm.service

import apm.mapper.ScheduleMapper
import apm.payload.ApiResponse
import apm.payload.ScheduleRequest
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class ScheduleService {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    enum class IND(val value: String) {
        ARR("ARR"),
        DEP("DEP"),
        AD("A/D")
    }

    enum class PORT {
        ICN,
        GMP
    }

    enum class SORT {
        ETA,
        ETD,
        SPT
    }

    enum class SCOPE {
        ALL,
        MY
    }

    @Autowired
    lateinit var scheduleMapper: ScheduleMapper

    @Autowired
    lateinit var userService: UserService

    @GraphQLQuery(name = "schedules")
    fun getSchedule(timestmp: Long, ind: IND?, apo: PORT, sort: SORT?, scope: SCOPE?): ApiResponse {
        val param =  ScheduleRequest(
                        timestmp = timestmp,
                        ind = ind?.value,
                        apo = apo.name,
                        sort = sort?.name,
                        scope = scope?.name,
                        amId = userService.amId!!)

        log.debug("param => {}", param)

        val list: List<Map<String, Any>> = scheduleMapper.getSchedules(param)

        return ApiResponse(
                length = list.size,
                body = list
        )
    }

}