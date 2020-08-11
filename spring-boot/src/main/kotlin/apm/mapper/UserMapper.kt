package apm.mapper

import apm.domain.Auth
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param


@Mapper
interface UserMapper {

    fun selectLogin(@Param("username") username: String): Auth?

    fun updateLoginCount(@Param("username") username: String): Int
}