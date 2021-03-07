package com.internshala.foodorderingapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.util.ConnectionManager
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var imgAppLogo : ImageView
    lateinit var txtForgotPasswordLabel : TextView
    lateinit var etRegisterMobileNo : EditText
    lateinit var etRegisterEmail : EditText
    lateinit var btnNext : Button

    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar = findViewById(R.id.Toolbar)
        imgAppLogo = findViewById(R.id.imgAppLogo)
        txtForgotPasswordLabel = findViewById(R.id.txtForgotPasswordLabel)
        etRegisterMobileNo = findViewById(R.id.etRegisterMobileNo)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        btnNext = findViewById(R.id.btnNext)


        btnNext.setOnClickListener {

            if(etRegisterMobileNo.length() == 10)
                flag = true

            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()

            jsonParams.put("mobile_number" , etRegisterMobileNo.text.toString())
            jsonParams.put("email" , etRegisterEmail.text.toString())

            if(ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)){

                if(flag)
                {

                    val jsonRequest = object  : JsonObjectRequest(Method.POST , url , jsonParams ,  Response.Listener {

                        try {

                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")

                            println("Response  for Forgot Password is $it")

                            if (success) {
                                if (data1.getString("first_try").equals(true)) {

                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ForgotPasswordActivity1::class.java
                                    )
                                    intent.putExtra(
                                        "UserMobileNo",
                                        etRegisterMobileNo.text.toString()
                                    )
                                    startActivity(intent)
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Please Enter the Previous OTP",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ForgotPasswordActivity1::class.java
                                    )
                                    intent.putExtra(
                                        "UserMobileNo",
                                        etRegisterMobileNo.text.toString()
                                    )
                                    startActivity(intent)
                                    finish()


                                }

                            } else {

                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    data1.getString("errorMessage"),
                                    Toast.LENGTH_LONG
                                ).show()

                            }


                        }
                        catch(e : Exception){

                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Json Error Has Occurred",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    },Response.ErrorListener {

                        Toast.makeText(this@ForgotPasswordActivity , "Some Error has Occurred " , Toast.LENGTH_LONG).show()

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
                else
                    Toast.makeText(this@ForgotPasswordActivity , "Mobile Number Should be of 10 Digits" , Toast.LENGTH_LONG).show()

            }
            else
            {

                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)

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