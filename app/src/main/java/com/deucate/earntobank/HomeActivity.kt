package com.deucate.earntobank

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*



class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        //Navigation Bat
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val titleTv = nav_view.getHeaderView(0).findViewById(R.id.navHeaderTitle) as TextView
        val subTitleTv = nav_view.getHeaderView(0).findViewById(R.id.navHeaderSubTitle) as TextView
        titleTv.text = auth.currentUser!!.displayName
        subTitleTv.text = auth.currentUser!!.email


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_logout -> {
                auth.signOut()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
