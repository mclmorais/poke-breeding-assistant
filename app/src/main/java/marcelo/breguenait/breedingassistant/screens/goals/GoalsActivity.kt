package marcelo.breguenait.breedingassistant.screens.goals

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.goals_activity.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.application.BreedingAssistantApplication
import marcelo.breguenait.breedingassistant.screens.goals.injection.DaggerGoalsComponent
import javax.inject.Inject

class GoalsActivity : AppCompatActivity() {

    @Inject
    lateinit var goalsPresenter: GoalsPresenter

    private var goalsFragment: GoalsFragment? = null

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

        //If the goals fragment doesn't currently exist in the fragment manager, creates a new instance
        goalsFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as GoalsFragment?
        if (goalsFragment == null) {
            goalsFragment = GoalsFragment.newInstance()
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.content_frame, goalsFragment)
            transaction.commit()
        }
    }

    /**
     * Forces a fast update on the fragment's adapter before the transition animation finishes in order to ensure that the data shown in the activity
     * is up to date. This is needed in order to avoid a 'flicker' of the old data after the transition is done, due to the way transitions work.
     */
    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        goalsFragment?.fastUpdateGoals()
    }
}
