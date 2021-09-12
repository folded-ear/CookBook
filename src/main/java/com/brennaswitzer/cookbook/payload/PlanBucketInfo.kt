package com.brennaswitzer.cookbook.payload

import com.brennaswitzer.cookbook.domain.PlanBucket
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

class PlanBucketInfo {
    var id: Long? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var name: String? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var date: LocalDate? = null

    companion object {
        @JvmStatic
        fun from(bucket: PlanBucket): PlanBucketInfo {
            val info = PlanBucketInfo()
            info.id = bucket.id!!
            info.name = bucket.name
            info.date = bucket.date
            return info
        }
    }
}
