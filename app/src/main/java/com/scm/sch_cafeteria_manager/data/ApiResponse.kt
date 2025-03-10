package com.scm.sch_cafeteria_manager.data


// 디테일 API Response
data class D_API_Response(
    val status: String,
    val message: String,
    val data: DetailMenu
)

// 오늘의 메뉴 API Response
data class TM_API_Response(
    val status: String,
    val message: String,
    val data: List<Cafeteria>
)