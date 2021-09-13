package com.brennaswitzer.cookbook.payload

import com.brennaswitzer.cookbook.domain.PlanBucket
import com.brennaswitzer.cookbook.domain.Task
import com.brennaswitzer.cookbook.domain.TaskList
import com.brennaswitzer.cookbook.domain.TaskStatus
import com.brennaswitzer.cookbook.util.IdUtils
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.stream.Collectors
import java.util.stream.StreamSupport

class TaskInfo {
    var id: Long? = null
    var name: String? = null
    var notes: String? = null
    var status: TaskStatus? = null
    var parentId: Long? = null
    var aggregateId: Long? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var acl: AclInfo? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var buckets: List<PlanBucketInfo>? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var subtaskIds: LongArray? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var componentIds: LongArray? = null

    var quantity: Double? = null
    var units: String? = null
    var uomId: Long? = null
    var ingredientId: Long? = null
    var bucketId: Long? = null
    var preparation: String? = null

    fun hasSubtasks(): Boolean {
        return subtaskIds != null && subtaskIds!!.size > 0
    }

    companion object {
        @JvmStatic
        fun fromTask(task: Task): TaskInfo {
            val info = TaskInfo()
            info.id = task.id
            info.name = task.name
            if (task.hasNotes()) {
                info.notes = task.notes
            }
            info.status = task.status
            info.parentId = task.parent?.id
            if (task.hasSubtasks()) {
                info.subtaskIds = IdUtils.toIdList(task.orderedSubtasksView)
            }
            info.aggregateId = task.aggregate?.id
            if (task.hasComponents()) {
                info.componentIds = IdUtils.toIdList(task.orderedComponentsView)
            }
            val ing = task.ingredient
            if (ing != null) {
                info.ingredientId = ing.id
                val q = task.quantity
                if (q != null) {
                    info.quantity = q.quantity
                    val u = q.units
                    if (u != null) {
                        info.uomId = u.id
                        info.units = u.name
                    }
                }
                info.preparation = task.preparation
            }
            if (task.hasBucket()) {
                info.bucketId = task.bucket!!.id
            }
            return info
        }

        @JvmStatic
        fun fromList(list: TaskList): TaskInfo {
            return fromPlan(list)
        }

        @JvmStatic
        fun fromPlan(plan: TaskList): TaskInfo {
            val info = fromTask(plan)
            info.acl = AclInfo.fromAcl(plan.acl)
            if (plan.hasBuckets()) {
                info.buckets = plan.buckets!!.stream()
                    .map { obj: PlanBucket? -> PlanBucketInfo.from(obj!!) }
                    .collect(Collectors.toList())
            }
            return info
        }

        @JvmStatic
        fun fromTasks(tasks: Iterable<Task>): List<TaskInfo> {
            return StreamSupport.stream(tasks.spliterator(), false)
                .map { task: Task -> fromTask(task) }
                .collect(Collectors.toList())
        }

        @JvmStatic
        fun fromLists(lists: Iterable<TaskList>): List<TaskInfo> {
            return fromPlans(lists)
        }

        @JvmStatic
        fun fromPlans(plans: Iterable<TaskList>): List<TaskInfo> {
            return StreamSupport.stream(plans.spliterator(), false)
                .map { list: TaskList -> fromList(list) }
                .collect(Collectors.toList())
        }
    }
}
