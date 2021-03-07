package com.internshala.foodorderingapplication.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.foodorderingapplication.R
import com.internshala.foodorderingapplication.adapter.RestaurantMenuRecyclerAdapter
import com.internshala.foodorderingapplication.database.FoodItems
import com.internshala.foodorderingapplication.fragment.*


class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences

    lateinit var txtProfileName: TextView
    lateinit var txtProfilePhoneNo: TextView
    lateinit var imgProfile : ImageView

    var previous_MenuItem: MenuItem? = null

    var username: String? = "John Doe"
    var usermobile: String? = "+91-1115555555"

    var flag = false
    var check_item = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.Toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)


        username = sharedPreferences.getString("UserName", "John Doe")
        usermobile = sharedPreferences.getString("UserMobileNumber", "+91-1115555555")


        val navView = navigationView.inflateHeaderView(R.layout.header_drawer)
        txtProfileName = navView.findViewById(R.id.txtProfileName)
        txtProfilePhoneNo = navView.findViewById(R.id.txtProfilePhoneNo)
        imgProfile = navView.findViewById(R.id.imgProfile)
        txtProfileName.text = username
        txtProfilePhoneNo.text = usermobile

        imgProfile.setOnClickListener {


            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, MyProfileFragment())
                .commit()

            supportActionBar?.title = "My Profile"
            drawerLayout.closeDrawers()


        }

        txtProfileName.setOnClickListener {


            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, MyProfileFragment())
                .commit()

            supportActionBar?.title = "My Profile"
            drawerLayout.closeDrawers()


        }

        txtProfilePhoneNo.setOnClickListener {


            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, MyProfileFragment())
                .commit()

            supportActionBar?.title = "My Profile"
            drawerLayout.closeDrawers()


        }

        setUpToolbar()
        openHome()

        println("Username is $username")
        println("Mobile Number is $usermobile")

        flag = intent.getBooleanExtra("order_success", false)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previous_MenuItem != null)
                previous_MenuItem?.isChecked = false

            it.isCheckable = true
            it.isChecked = true
            previous_MenuItem = it

            when (it.itemId) {

                R.id.Home -> {

                    openHome()

                }

                R.id.MyProfile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, MyProfileFragment())
                        .commit()

                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()

                }

                R.id.FavouriteRestaurants -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouriteRestaurantsFragment())
                        .commit()
                    supportActionBar?.setHomeButtonEnabled(true)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)

                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()

                }

                R.id.OrderHistory -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()

                }

                R.id.Faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FAQsFragment())
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()

                }

                R.id.Logout -> {

                    //Toast.makeText(this@MainActivity,"Logout Clicked",Toast.LENGTH_SHORT).show()
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setNegativeButton("No") { text, listener ->
                        //Do Nothing
                    }
                    dialog.setPositiveButton("Yes") { text, listener ->

                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    dialog.create()
                    dialog.show()
                }


            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "FOOD JUNCTION"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }


    fun openHome() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.Home)
        drawerLayout.closeDrawers()
    }

    
    @SuppressLint("CommitPrefEdits")
    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
         check_item = sharedPreferences.getBoolean("item_clicked" , false)

        if (!flag) {
            when (frag) {

                !is HomeFragment -> {

                    if(frag is RestaurantMenuFragment && check_item){

                        val bundle = intent.getBundleExtra("data")
                        val  resId = bundle?.getString("res_Id").toString()

                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Confirmation")
                        dialog.setMessage("Going back will reset cart items.Do you still want to proceed?")
                        dialog.setNegativeButton("No") { text, listener ->


                        }
                        dialog.setPositiveButton("Yes") { text, listener ->

                            CartActivity.DeleteOrder(this@MainActivity , resId)
                            sharedPreferences.edit().putBoolean("item_clicked" , false).apply()
                            openHome()

                        }

                        dialog.create()
                        dialog.show()

                    }
                    else
                    openHome()
                }

                else -> super.onBackPressed()

            }


        } else {

            when (frag) {

                !is HomeFragment -> {

                    if(frag is RestaurantMenuFragment && check_item){

                        val bundle = intent.getBundleExtra("data")
                        val  resId = bundle?.getString("res_Id").toString()

                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Confirmation")
                        dialog.setMessage("Going back will reset cart items.Do you still want to proceed?")
                        dialog.setNegativeButton("No") { text, listener ->


                        }
                        dialog.setPositiveButton("Yes") { text, listener ->

                            CartActivity.DeleteOrder(this@MainActivity , resId)
                            sharedPreferences.edit().putBoolean("item_clicked" , false).apply()
                            openHome()

                        }

                        dialog.create()
                        dialog.show()

                    }
                    else
                        openHome()
                }
                else -> ActivityCompat.finishAffinity(this)

            }

        }

    }
}