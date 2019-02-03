package com.deucate.earntobank

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import android.content.ActivityNotFoundException
import android.net.Uri
import com.deucate.earntobank.home.HomeFragment
import com.deucate.earntobank.task.TaskFragment
import de.hdodenhof.circleimageview.CircleImageView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val auth = FirebaseAuth.getInstance()

    private val currentFragment = MutableLiveData<Fragment>()
    private var currentFragmentID = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Navigation Bat
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val titleTv = nav_view.getHeaderView(0).findViewById(R.id.navHeaderTitle) as TextView
        val subTitleTv = nav_view.getHeaderView(0).findViewById(R.id.navHeaderSubTitle) as TextView
        val profilePictureView = nav_view.getHeaderView(0).findViewById(R.id.profilePictureIV) as CircleImageView
        titleTv.text = auth.currentUser!!.displayName
        subTitleTv.text = auth.currentUser!!.email
        Picasso.get().load(auth.currentUser!!.photoUrl).fit().into(profilePictureView)

        currentFragment.observe(this, Observer { rootIt ->
            rootIt?.let {
                if (it.id != currentFragmentID) {
                    supportFragmentManager.beginTransaction().replace(R.id.container, it, null).commit()
                    currentFragmentID = it.id
                }
            }
        })

        currentFragment.value = HomeFragment()

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
                AlertDialog.Builder(this).setTitle("Warning!!")
                    .setMessage("Are you sure you want to logout from this app?").setPositiveButton("YES") { _, _ ->
                        auth.signOut()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finish()
                    }
            }

            R.id.nav_share -> {
                val shareBody = "I found amazing app. Download to earn money"
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here")
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "Share With"))
            }

            R.id.nav_rate_us -> {
                val uri = Uri.parse("market://details?id=$packageName")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }

            }

            R.id.nav_home -> {
                currentFragment.value = HomeFragment()
            }
            R.id.nav_task -> {
                currentFragment.value = TaskFragment()
            }
            R.id.nav_pocket -> {
                currentFragment.value = PocketFragment()
            }
            R.id.nav_redeem -> {
                currentFragment.value = RedeemFragment()
            }
            R.id.nav_history -> {
                currentFragment.value = HistoryFragment()
            }
            R.id.nav_group -> {
                currentFragment.value = GroupFragment()
            }
            R.id.nav_alert -> {
                currentFragment.value = AlertFragment()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
