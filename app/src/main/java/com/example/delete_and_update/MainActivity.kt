package com.example.delete_and_update

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_UPDATE_CAR = 100
    }

    private var Voitures: List<Car> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getData()
        addData()
    }

    private fun getData() {
        val listView = findViewById<ListView>(R.id.lv)
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedCar = Voitures[position]
            val intent = Intent(this, updateActivity::class.java)
            intent.putExtra("car_id", selectedCar.id)
            intent.putExtra("car_name", selectedCar.name)
            intent.putExtra("car_price", selectedCar.price)
            intent.putExtra("car_image", selectedCar.image)
            intent.putExtra("car_isFulloption", selectedCar.isFullOptions)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_CAR)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(Api::class.java)

        val call = apiService.getCars()
        call.enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.isSuccessful) {
                    Voitures = response.body() ?: emptyList()
                    val carNames = mutableListOf<String>()
                    for (c in Voitures) {
                        carNames.add("${c.name} - ${c.price} MAD")
                    }

                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, carNames)
                    listView.adapter = adapter
                } else {
                    Toast.makeText(this@MainActivity, "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to connect to API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addData() {
        val editName = findViewById<EditText>(R.id.editText1)
        val editPrice = findViewById<EditText>(R.id.editText2)
        val editImageUrl = findViewById<EditText>(R.id.editText3)
        val checkbox = findViewById<CheckBox>(R.id.checkBox)
        val buttonAddCar = findViewById<Button>(R.id.buttonAdd)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(Api::class.java)

        buttonAddCar.setOnClickListener {
            val name = editName.text.toString().trim()
            val priceStr = editPrice.text.toString().trim()
            val imageUrl = editImageUrl.text.toString().trim()
            val isFulloption = checkbox.isChecked

            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val price = priceStr.toDouble()
                val car = Car(0, name, price, imageUrl, isFulloption)
                apiService.addCar(car).enqueue(object : Callback<AddResponse> {
                    override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                        if (response.isSuccessful) {
                            val addResponse = response.body()
                            if (addResponse != null) {
                                Toast.makeText(applicationContext, addResponse.status_message, Toast.LENGTH_LONG).show()
                                if (addResponse.status == 1) {
                                    getData()
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Failed to add car", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_CAR && resultCode == RESULT_OK) {
            getData()
        }
    }
}