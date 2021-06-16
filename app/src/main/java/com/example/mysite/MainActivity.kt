package com.example.mysite

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings.Global.getString
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionBarContainer
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Parcelable {
    var networkAvailabel = false
    lateinit var mWebView: WebView
    lateinit var drawerLayout1 : DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    constructor(parcel: Parcel) : this() {
        networkAvailabel = parcel.readByte() != 0.toByte()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        var url = getString(R.string.website_url)
        var urlFeedBack = getString(R.string.website_urlFeedBack)
        drawerLayout1 = findViewById(R.id.drawer_layout)
        val NavView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout1, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout1.addDrawerListener(toggle)
        toggle.syncState()
        NavView.setNavigationItemSelectedListener(this)

        mWebView = findViewById(R.id.webView)
val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(false)

        loadWebSite(mWebView, url, applicationContext)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed, R.color.colorBlue, R.color.colorGreen)
        swipeRefreshLayout.apply {
            setOnRefreshListener {
                if (mWebView.url != null) url = mWebView.url!!
                loadWebSite(mWebView, url, applicationContext)
            }
            setOnChildScrollUpCallback { parent, child -> mWebView.getScrollY() > 0  }
        }
//    webView.webViewClient = WebViewClient()
//        webView.loadUrl(url)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { loadWebSite(mWebView, urlFeedBack,applicationContext)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val url = getString(R.string.website_url)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_kt -> {
                val url = getString(R.string.website_kt)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_opl -> {
                val url = getString(R.string.website_opl)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_dost -> {
                val url = getString(R.string.website_dost)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_con -> {
                val url = getString(R.string.website_urlFeedBack)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_nov -> {
                val url = getString(R.string.website_nov)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_ak -> {
                val url = getString(R.string.website_ak)
                loadWebSite(mWebView, url, applicationContext)
            }
        }
        drawerLayout1.closeDrawer(GravityCompat.START)
        return true
    }
@RequiresApi(Build.VERSION_CODES.O)
private fun loadWebSite(mWebView: WebView, url: String, context: Context){
    progressBar.visibility = View.VISIBLE
    networkAvailabel = isNetworkAvailable(context)
    mWebView.clearCache(true)
    if (networkAvailabel) {
        wvVisible(mWebView)
        mWebView.webViewClient = MyWebViewClient()
        mWebView.loadUrl(url)
    } else{
        wvGone(mWebView)
    }
}
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun wvVisible(mWebView: WebView) {
        mWebView.visibility = View.VISIBLE
        tvCheckConnection.visibility = View.GONE
    }

    private fun wvGone(mWebView: WebView) {
        mWebView.visibility = View.GONE
        tvCheckConnection.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    //обработка отсутствия подключения
    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT > 22) {
                val an = cm.activeNetwork ?: return false
                val capabilities = cm.getNetworkCapabilities(an) ?: return false
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val a = cm.activeNetworkInfo ?: return false
                a.isConnected && (a.type == ConnectivityManager.TYPE_WIFI || a.type == ConnectivityManager.TYPE_MOBILE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false

    }
    //перехват ошибок сервера и остановка програсс бара
private fun onLoadComplete(){
    swipeRefreshLayout.isRefreshing = false
        progressBar.visibility = View.GONE
}

    private inner class MyWebViewClient : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url.toString()
            return urlOverride(url)


        }

        private fun urlOverride(url: String): Boolean {
            progressBar.visibility = View.VISIBLE
            networkAvailabel = isNetworkAvailable(applicationContext)

            if (networkAvailabel) {
                if (Uri.parse(url).host == getString(R.string.website_domain)) return false
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                onLoadComplete()
                return true
            } else {
                wvGone(webView)
                return false
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {

            return urlOverride(url)

        }
@Suppress("DEPRECATION")
        override fun onReceivedError(view: WebView?,errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
    if (errorCode == 0) {
        view?.visibility = View.GONE
        tvCheckConnection.visibility = View.VISIBLE
        onLoadComplete()
    }
}
@TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
           onReceivedError(view, error!!.errorCode, error.description.toString(), request!!.url.toString())
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onLoadComplete()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (networkAvailabel) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
}
//обработка внешних ссылок
//private  inner class MyWebViewClient : WebViewClient() {
//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        val url = request?.url.toString()
//        progressBar.visibility = View.VISIBLE
//        networkAvailable = isNetworkAvailable(applicationContext)
//
//        if (networkAvailable) {
//            if (Uri.parse(url).host == getString(R.string.website_domain)) return false
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        }
//    }
//}
//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        val url = request?.url.toString()
//        progressBar.visibility = View.VISIBLE
//        networkAvailable = isNetworkAvailable(applicationContext)
//
//        if (networkAvailable){
//            if(Uri.parse(url).host == getString(R.string.website_domain)) return false
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        }
//    }

