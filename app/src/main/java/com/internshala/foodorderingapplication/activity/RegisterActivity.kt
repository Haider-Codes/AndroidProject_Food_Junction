package com.internshala.foodorderingapplication.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var imgAppLogo : ImageView
    lateinit var etRegisterName : EditText
    lateinit var etRegisterEmail : EditText
    lateinit var etRegisterMobileNo : EditText
    lateinit var etRegisterAddress : EditText
    lateinit var etRegisterPassword : EditText
    lateinit var etConfirmRegisterPassword : EditText
    lateinit var btnRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar = findViewById(R.id.Toolbar)
        imgAppLogo = findViewById(R.id.imgAppLogo)
        etRegisterName = findViewById(R.id.etRegisterName)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterMobileNo = findViewById(R.id.etRegisterMobileNo)
        etRegisterAddress = findViewById(R.id.etRegisterAddress)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etConfirmRegisterPassword = findViewById(R.id.etConfirmRegisterPassword)
        btnRegister = findViewById(R.id.btnRegister)

        val email = etRegisterEmail.text

        var flag: Boolean

        val sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)

        setUpToolbar()

        toolbar.setNavigationOnClickListener {

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        btnRegister.setOnClickListener {

            if (etRegisterName.length() > 2) {

                flag = true


                    if (etRegisterMobileNo.length() == 10) {

                        flag = true

                        if (etRegisterPassword.length() > 3) {

                            flag =true

                            if (etConfirmRegisterPassword.text.toString() != etRegisterPassword.text.toString()) {

                                flag =false

                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Password and Confirm Password fields should be same",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else
                                flag =true
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Password Should be of minimum 4 characters",
                                Toast.LENGTH_SHORT
                            ).show()
                            flag = false
                        }

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Mobile Number Should be of 10 digits",
                            Toast.LENGTH_SHORT
                        ).show()
                        flag = false
                    }

                }

            else {
                Toast.makeText(this@RegisterActivity, "Invalid Name", Toast.LENGTH_SHORT).show()
                flag = false
            }


            println("Flag is $flag")
            println("Email is ${etRegisterEmail.text.toString()}")
            val queue = Volley.newRequestQueue(this@RegisterActivity)
            val url = "http://13.235.250.119/v2/register/fetch_result"

            val jsonParams = JSONObject()

            jsonParams.put("name", etRegisterName.text.toString())
            jsonParams.put("mobile_number", etRegisterMobileNo.text.toString())
            jsonParams.put("password", etRegisterPassword.text.toString())
            jsonParams.put("address", etRegisterAddress.text.toString())
            jsonParams.put("email", etRegisterEmail.text.toString())

            if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {

                if (flag) {

                    val jsonRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {

                                println("Response  for Registration is $it")

                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if (success) {

                                    println("Success is $success")

                                    val info = data1.getJSONObject("data")

                                    sharedPreferences.edit()
                                        .putString("UserId", info.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("UserName", info.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("UserEmail", info.getString("email")).apply()
                                    sharedPreferences.edit().putString(
                                        "UserMobileNumber",
                                        info.getString("mobile_number")
                                    ).apply()
                                    sharedPreferences.edit()
                                        .putString("UserAddress", info.getString("address"))
                                        .apply()

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()


                                } else
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        data1.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()

                            }
                            catch(e : Exception){

                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Json Error Has Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }, Response.ErrorListener {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Some Error Has Occurred",
                                Toast.LENGTH_SHORT
                            ).show()

                        }) {

                            override fun getHeaders(): MutableMap<String, String> {

                                val headers = HashMap<String, String>()
                                headers["Content-Type"] = "application/json"
                                headers["token"] = "4f1fadec00b2ac"
                                return headers

                            }


                        }

                    queue.add(jsonRequest)




                }

            } else {

                val dialog = AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(this@RegisterActivity)

                }
                dialog.setPositiveButton("Open Settings") { text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this.finish()


                }

                dialog.create()
                dialog.show()


            }


        }
    }

    fun setUpToolbar(){

        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}