package apm.mapper

import apm.payload.ScheduleRequest
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ScheduleMapper {

    fun getSchedules(param: ScheduleRequest): List<Map<String, Any>>
}
