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
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity1 : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var txtForgotPassword1Label : TextView
    lateinit var etEnterOTP : EditText
    lateinit var etNewPassword : EditText
    lateinit var etConfirmNewPassword : EditText
    lateinit var btnSubmit : Button

    lateinit var sharedPreferences: SharedPreferences

    var flag = false

    var mobileNumber : String? = "91-1115555555"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password1)

        if(intent != null)
            mobileNumber = intent.getStringExtra("UserMobileNo")

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.Toolbar)
        txtForgotPassword1Label = findViewById(R.id.txtForgotPassword1Label)
        etEnterOTP = findViewById(R.id.etEnterOtp)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)


        btnSubmit.setOnClickListener {

          if(etNewPassword.length() > 3) {
              flag = true
          if(etNewPassword.text.toString() == etConfirmNewPassword.text.toString())
              flag = true
              else
          {
              flag = false
              Toast.makeText(this@ForgotPasswordActivity1 , "Password and Confirm Password Should be same" , Toast.LENGTH_SHORT).show()

          }

          }
            else{

              flag = false
              Toast.makeText(this@ForgotPasswordActivity1 , "Password should be of minimum 4 characters" , Toast.LENGTH_SHORT).show()

          }

            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity1)
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"

            val jsonParams = JSONObject()

            jsonParams.put("mobile_number" , mobileNumber)
            jsonParams.put("password" , etNewPassword.text.toString())
            jsonParams.put("otp" , etEnterOTP.text.toString())

            if(ConnectionManager().checkConnectivity(this@ForgotPasswordActivity1)){

                if(flag){

                    val jsonRequest = object : JsonObjectRequest(Method.POST , url , jsonParams , Response.Listener {

                        try {

                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")

                            if (success) {

                                Toast.makeText(
                                    this@ForgotPasswordActivity1,
                                    data1.getString("successMessage"),
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedPreferences.edit().clear().apply()
                                val intent =
                                    Intent(this@ForgotPasswordActivity1, LoginActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {

                                Toast.makeText(
                                    this@ForgotPasswordActivity1,
                                    "Some Error has Occurred ",
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                        }
                        catch(e : Exception){

                            Toast.makeText(
                                this@ForgotPasswordActivity1,
                                "Json Error Has Occurred",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    },Response.ErrorListener {

                        Toast.makeText(this@ForgotPasswordActivity1 , "Some Error Has Occurred" , Toast.LENGTH_LONG).show()

                    }){

                        override fun getHeaders(): MutableMap<String, String> {

                            val headers = HashMap<String , String>()
                            headers["Content-Type"] = "application/json"
                            headers["token"] = "4f1fadec00b2ac"
                            return headers

                        }

                    }

                    queue.add(jsonRequest)

                }

                }
            else
            {

                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity1)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity1)

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
}