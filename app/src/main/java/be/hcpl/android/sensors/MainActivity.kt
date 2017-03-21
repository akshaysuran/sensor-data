package be.hcpl.android.sensors

import androidx.appcompat.app.AppCompatActivity
import be.hcpl.android.sensors.NavigationDrawerFragment.NavigationDrawerCallbacks
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hcpl.android.sensors.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private var mNavigationDrawerFragment: NavigationDrawerFragment? = null
    private lateinit var mBinding: ActivityMainBinding
    /**
     * this is the one and only visible fragment loaded in the container
     */
    private var mContentFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set the layout here for our acitivity
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // the fragment loaded in the navigation drawer
        mNavigationDrawerFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_drawer) as NavigationDrawerFragment?

        // Set up the drawer
        mNavigationDrawerFragment?.setUp(R.id.navigation_drawer,mBinding.drawerLayout)
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        // get the selected fragment from the navigation drawer
        mNavigationDrawerFragment?.let { it ->
            mContentFragment = it.navigationFragments.getItem(position) as Fragment
            mContentFragment.let {
                mContentFragment = WelcomeFragment()
            }
        }
        mContentFragment?.let { switchFragment(it) }
    }

    /**
     * public method that allows switching the content fragment. This way fragments can
     * request switching to another fragments (= perform navigation)
     *
     * @param fragment
     */
    fun switchFragment(fragment: Fragment) {
        // update the main content by replacing fragments
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
        mContentFragment = fragment
    }

    private fun restoreActionBar() {
        val actionBar = supportActionBar
        actionBar?.navigationMode = ActionBar.NAVIGATION_MODE_STANDARD
        actionBar?.setDisplayShowTitleEnabled(true)
        //  actionBar.setTitle(mTitle);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mNavigationDrawerFragment?.isDrawerOpen == false) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            menuInflater.inflate(R.menu.main, menu)
            restoreActionBar()
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}