package com.workingagenda.democracydroid.screens.main.mvp

import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.screens.about.AboutActivity
import com.workingagenda.democracydroid.screens.download.DownloadFragment
import com.workingagenda.democracydroid.screens.main.MainActivity
import com.workingagenda.democracydroid.screens.podcast.PodcastFragment
import com.workingagenda.democracydroid.screens.settings.SettingsActivity
import com.workingagenda.democracydroid.screens.story.StoryFragment

/**
 * Created by derrickrocha on 12/10/17.
 */
class MainModel(private var mActivity:MainActivity) {

    fun onOptionsItemSelected(item: MenuItem): Boolean{
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(mActivity, SettingsActivity::class.java)
            mActivity.startActivity(intent)
            return true
        }
        if (id == R.id.action_refresh) {
            // Don't let user click before async tasks are done
            item.isEnabled = false
            // Call Fragment refresh methods
            mActivity.supportFragmentManager.fragments
            for (x in mActivity.supportFragmentManager.fragments) {
                (x as? PodcastFragment)?.refresh()
                (x as? StoryFragment)?.refresh()
                (x as? DownloadFragment)?.refresh()
            }
            // FIXME: Somehow enable this after async call...
            item.isEnabled = true
            return true
        }
        if (id == R.id.action_donate) {
            val url = "https://www.democracynow.org/donate"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mActivity.startActivity(i)
            return true
        }
        if (id == R.id.action_exclusives) {
            val url = "https://www.democracynow.org/categories/web_exclusive"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mActivity.startActivity(i)
            return true
        }
        if (id == R.id.action_site) {
            val url = "http://www.democracynow.org/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mActivity.startActivity(i)
            return true
        }
        if (id == R.id.action_about) {
            val intent = Intent(mActivity, AboutActivity::class.java)
            mActivity.startActivityForResult(intent, 0)
        }
        return true
    }

    fun onCreateOptionsMenu(menu: Menu): Boolean {
        mActivity.menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}