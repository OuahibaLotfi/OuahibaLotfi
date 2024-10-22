package com.example.delete_and_update

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class updateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val editTextName = findViewById<EditText>(R.id.EditeText1)
        val editTextPrice = findViewById<EditText>(R.id.Price)
        val editTextImageUrl = findViewById<EditText>(R.id.imgurl)
        val checkbox = findViewById<CheckBox>(R.id.FullOp)
        val buttonUpdateCar = findViewById<Button>(R.id.ButtonUp)
        val buttonDeleteCar = findViewById<Button>(R.id.ButtonSup)

        val IdCar = intent.getIntExtra("car_id", 0)
        val carName = intent.getStringExtra("car_name")
        val carPrice = intent.getDoubleExtra("car_price", 0.0)
        val carImage = intent.getStringExtra("car_image")
        val IsFulloption = intent.getBooleanExtra("car_isFulloption", false)

        editTextName.setText(carName)
        editTextPrice.setText(carPrice.toString())
        editTextImageUrl.setText(carImage)
        checkbox.isChecked = IsFulloption

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(Api::class.java)

        buttonUpdateCar.setOnClickListener {
            val UpName = editTextName.text.toString().trim()
            val UpPrice = editTextPrice.text.toString().trim()
            val Img = editTextImageUrl.text.toString().trim()
            val isFullOption = checkbox.isChecked

            if (UpName.isEmpty() || UpPrice.isEmpty() || Img.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val updatedPrice = UpPrice.toDouble()

                val updatedCar = Car(IdCar, UpName, updatedPrice, Img, isFullOption)

                apiService.updateCar(updatedCar).enqueue(object : Callback<AddResponse> {
                    override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@updateActivity, "Car updated successfully", Toast.LENGTH_LONG).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@updateActivity, "Failed to update car", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        Toast.makeText(this@updateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }


        buttonDeleteCar.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://apiyes.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService1 = retrofit.create(Api::class.java)
            val carToDelete = Car(id = IdCar, name = "", price = 0.0, image = "",isFullOptions = false)



            apiService1.deleteCar(carToDelete).enqueue(object : Callback<AddResponse> {
                override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@updateActivity, "Car deleted successfully", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@updateActivity, "Failed to delete car", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                    Toast.makeText(this@updateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}