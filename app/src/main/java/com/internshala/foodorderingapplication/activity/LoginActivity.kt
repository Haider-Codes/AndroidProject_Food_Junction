package com.internshala.foodorderingapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.fragment.HomeFragment
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var toolbar : Toolbar
    lateinit var etLoginMobileNo : EditText
    lateinit var etLoginPassword : EditText
    lateinit var btnLogin : Button
    lateinit var txtForgotPassword : TextView
    lateinit var txtRegister : TextView

    var  flag = true


    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toolbar = findViewById(R.id.Toolbar)
        etLoginMobileNo = findViewById(R.id.etLoginMobileNo)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        if(isLoggedIn){

            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        btnLogin.setOnClickListener {


            if(etLoginMobileNo.length() == 10){

                flag = true

                if(etLoginPassword.length() < 4) {
                    flag = false
                    Toast.makeText(
                        this@LoginActivity,
                        "Password Should be of minimum 4 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else
                    flag = true

              /*  val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()

                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()

*/
            }
            else {
                flag = false
                Toast.makeText(
                    this@LoginActivity,
                    "Mobile Number Should be of 10 Digits",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number" , etLoginMobileNo.text.toString())
            jsonParams.put("password" , etLoginPassword.text.toString())

            if (ConnectionManager().checkConnectivity(this@LoginActivity)){

                if(flag){

                    val jsonRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {
                                println("Flag is $flag")
                                println("Response  for Login is $it")

                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if (success) {

                                    println("Success is $success")

                                    val loginInfo = data1.getJSONObject("data")

                                    sharedPreferences.edit()
                                        .putString("UserId", loginInfo.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("UserName", loginInfo.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("UserEmail", loginInfo.getString("email"))
                                        .apply()
                                    sharedPreferences.edit().putString(
                                        "UserMobileNumber",
                                        loginInfo.getString("mobile_number")
                                    ).apply()
                                    sharedPreferences.edit()
                                        .putString("UserAddress", loginInfo.getString("address"))
                                        .apply()

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else
                                    Toast.makeText(
                                        this@LoginActivity,
                                        data1.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()

                            }
                            catch(e : Exception){

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Json Error has Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        },Response.ErrorListener {

                            Toast.makeText(
                                this@LoginActivity,
                                "Some Error Has Occurred",
                                Toast.LENGTH_SHORT
                            ).show()

                        }){

                            override fun getHeaders(): MutableMap<String, String> {

                                val headers = HashMap<String, String>()
                                headers["Content-Type"] = "application/json"
                                headers["token"] = "4f1fadec00b2ac"
                                return headers

                            }

                        }

                    queue.add(jsonRequest)

                        }

            }
            else {

                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(this@LoginActivity)

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

        txtRegister.setOnClickListener {

            val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

        txtForgotPassword.setOnClickListener {

            val intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

    }

}

