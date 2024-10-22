package com.example.delete_and_update
import com.example.delete_and_update.AddResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class Car(
    val id: Int,
    val name: String,
    val price: Double,
    val image: String,
    val isFullOptions: Boolean
)

data class AddResponse(
    val status: Int,
    val status_message: String
)

interface Api {
    @GET("CarAPI/readAll.php")
    fun getCars(): Call<List<Car>>

    @POST("CarAPI/create.php")
    fun addCar(@Body car: Car): Call<AddResponse>

    @POST("CarAPI/update.php")
    fun updateCar(@Body car: Car): Call<AddResponse>

    @POST("CarAPI/delete.php")
    fun deleteCar(@Body car: Car): Call<AddResponse>
}