package com.scm.sch_cafeteria_manager.data

sealed class ApiResponse<out T>{
    data class Success<T>(val data: T): ApiResponse<T>()
    data class Error(val message: String, val errorCode: Int? = null): ApiResponse<Nothing>()
    object UnknownError: ApiResponse<Nothing>()
}