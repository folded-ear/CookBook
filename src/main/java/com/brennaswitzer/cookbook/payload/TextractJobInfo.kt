package com.brennaswitzer.cookbook.payload

import com.brennaswitzer.cookbook.domain.TextractJob
import com.brennaswitzer.cookbook.services.StorageService
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class TextractJobInfo {
    var id: Long? = null
    var photo: FileInfo? = null
    var ready = false
    var lines: Set<TextractJob.Line>? = null

    companion object {

        @JvmStatic
        fun fromJob(
            job: TextractJob,
            storageService: StorageService
        ): TextractJobInfo {
            val info = TextractJobInfo()
            info.id = job.id
            info.photo = FileInfo.fromS3File(job.photo!!, storageService)
            info.ready = job.ready
            return info
        }

        @JvmStatic
        fun fromJobWithLines(
            job: TextractJob,
            storageService: StorageService
        ): TextractJobInfo {
            val info = fromJob(job, storageService)
            if (job.ready) {
                info.lines = job.lines
            }
            return info
        }
    }
}
