package com.brennaswitzer.cookbook.domain

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull

@Entity
@Table(
    name = "plan_bucket",
    uniqueConstraints = [UniqueConstraint(columnNames = ["plan_id", "name"])]
)
class PlanBucket : BaseEntity {
    @ManyToOne
    var plan: @NotNull TaskList? = null
    var name: String? = null
    var date: LocalDate? = null

    constructor() {}
    constructor(name: String?) {
        this.name = name
    }
}
