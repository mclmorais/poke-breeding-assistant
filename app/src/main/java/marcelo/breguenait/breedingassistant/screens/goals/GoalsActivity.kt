package marcelo.breguenait.breedingassistant.screens.goals

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.goals_activity.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.application.BreedingAssistantApplication
import marcelo.breguenait.breedingassistant.extensions.active
import marcelo.breguenait.breedingassistant.extensions.inAnimatedTransaction
import marcelo.breguenait.breedingassistant.extensions.inTransaction
import marcelo.breguenait.breedingassistant.screens.assistant.BoxContract
import marcelo.breguenait.breedingassistant.screens.assistant.StoredPokemonFragment
import marcelo.breguenait.breedingassistant.screens.goals.injection.DaggerGoalsComponent
import javax.inject.Inject

class GoalsActivity :
    AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    StoredPokemonFragment.PresenterCallback {


    @Inject
    lateinit var goalsPresenter: GoalsPresenter

    private var goalsFragment: GoalsFragment? = null

    private var navPosition = NAV_GOALS


    private fun initBottomNavigation() {
        bottom_navigation.active(navPosition)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_activity)

        //Builds Dagger injection components
        DaggerGoalsComponent.builder()
            .applicationComponent(BreedingAssistantApplication.get(this).component)
            .build()
            .inject(this)

        //Sets the custom XML toolbar as the action bar
        setSupportActionBar(toolbar)

        //Adds the version name next to the title. If the version name can't be found, leaves it empty
        activity_version_label.text = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }


//        //If the goals fragment doesn't currently exist in the fragment manager, creates a new instance
//        goalsFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as GoalsFragment?
//        if (goalsFragment == null) {
//            goalsFragment = GoalsFragment.newInstance()
//            val fragmentManager = supportFragmentManager
//            val transaction = fragmentManager.beginTransaction()
//            transaction.add(R.id.content_frame, goalsFragment)
//            transaction.commit()
//        }

        initBottomNavigation()
        savedInstanceState ?: switchFragment(NAV_GOALS)
    }

    /**
     * Forces a fast update on the fragment's adapter before the transition animation finishes in order to ensure that the data shown in the activity
     * is up to date. This is needed in order to avoid a 'flicker' of the old data after the transition is done, due to the way transitions work.
     */
    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        goalsFragment?.fastUpdateGoals()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Store the current navigation position.
        outState?.putInt(KEY_POSITION, navPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        // Restore the current navigation position.
        savedInstanceState?.let {
            val id = it.getInt(KEY_POSITION, NAV_GOALS)
            navPosition = when (id) {
                R.id.nav_goals    -> NAV_GOALS
                R.id.nav_box      -> NAV_BOX
                R.id.nav_tutorial -> NAV_TUTORIAL
                else              -> NAV_GOALS
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        navPosition = when (item.itemId) { //TODO: turn into function
            R.id.nav_goals    -> NAV_GOALS
            R.id.nav_box      -> NAV_BOX
            R.id.nav_tutorial -> NAV_TUTORIAL
            else              -> NAV_GOALS
        }

        return switchFragment(navPosition)

    }

    private fun switchFragment(position: Int): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(getTag(position)) ?: createFragment(position)
        if (fragment.isAdded) return false
        detachFragment()
        attachFragment(fragment, getTag(position))
        supportFragmentManager.executePendingTransactions()
        switchTitle(position)
        return true
    }


    companion object {
        const val KEY_POSITION = "key_position"

        const val NAV_GOALS = 0
        const val NAV_BOX = 1
        const val NAV_TUTORIAL = 2
    }

    private fun getTag(position: Int): String = when (position) {
        NAV_GOALS    -> GoalsFragment.TAG
        NAV_BOX      -> StoredPokemonFragment.TAG
        NAV_TUTORIAL -> TutorialFragment.TAG
        else         -> GoalsFragment.TAG
    }

    private fun createFragment(position: Int): Fragment = when (position) {
        NAV_GOALS    -> GoalsFragment.newInstance()
        NAV_BOX      -> StoredPokemonFragment.newInstance()
        NAV_TUTORIAL -> TutorialFragment.newInstance()
        else         -> GoalsFragment.newInstance()
    }

    private fun detachFragment() {
        supportFragmentManager.findFragmentById(R.id.content_frame)?.also {
            supportFragmentManager.beginTransaction().detach(it).commit()
        }
    }

    private fun attachFragment(fragment: Fragment, tag: String) {

        if(fragment.isDetached)
            supportFragmentManager.inAnimatedTransaction { attach(fragment) }
        else
            supportFragmentManager.inAnimatedTransaction { add(R.id.content_frame, fragment, tag) }
    }

    override fun setPresenter(): BoxContract.Presenter {
        return goalsPresenter
    }

    private fun switchTitle(position: Int) = when (position) {
        NAV_GOALS    -> {
            activity_title.text = getString(R.string.goals_activity_label)
            activity_background_symbol.setImageResource(R.drawable.ic_goal)
        }
        NAV_BOX      -> {
            activity_title.text = getString(R.string.box_activity_label)
            activity_background_symbol.setImageResource(R.drawable.ic_grid_on_black_24dp)
        }
        NAV_TUTORIAL -> {
            activity_title.text = getString(R.string.tutorial_activity_label)
            activity_background_symbol.setImageResource(R.drawable.ic_school_black_24dp)
        }
        else         -> {
        }
    }
}
