package com.hlasoftware.focus.features.workgroup_details.domain.model

data class WorkgroupTask(
    val id: String,
    val name: String,
    val implicatedMembers: List<WorkgroupMember>
)
