package com.scm.sch_cafeteria_manager.util

import com.scm.sch_cafeteria_manager.data.BASE_URL
import com.scm.sch_cafeteria_manager.data.TodayMenu
import retrofit2.Retrofit
import retrofit2.http.POST

interface ApiService_test {

    @POST("/test/getAllUser")
    suspend fun getAllUser(): String

}

//object RetrofitClient_test {
//    val apiSercive: ApiService_test by lazy {
//        try {
//            Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .add
//        }
//    }
//}