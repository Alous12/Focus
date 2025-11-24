package com.hlasoftware.focus.features.workgroup_details.domain.model

import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup

data class WorkgroupDetails(
    val workgroup: Workgroup = Workgroup(),
    val members: List<WorkgroupMember> = emptyList(),
    val tasks: List<WorkgroupTask> = emptyList(),
)

data class WorkgroupMember(
    val id: String = "",
    val name: String = "",
    val color: String = "#FFFFFF" // Color as a hex string
)
