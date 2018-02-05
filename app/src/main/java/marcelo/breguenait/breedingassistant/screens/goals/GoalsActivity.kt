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


    @Inject lateinit var goalsPresenter: GoalsPresenter

    private var goalsFragment: GoalsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_activity)

        //Sets the custom XML toolbar as the action bar
        setSupportActionBar(toolbar)

        //Adds the version name next to the title. If the version name can't be found, leaves it empty
        activity_version_label.text = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }

        goalsFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as GoalsFragment?
        if (goalsFragment == null) {
            goalsFragment = GoalsFragment.newInstance()
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.content_frame, goalsFragment)
            transaction.commit()
        }

        //Builds Dagger injection components
        DaggerGoalsComponent.builder()
                .applicationComponent(BreedingAssistantApplication.get(this).component)
                .build()
                .inject(this)

    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        goalsFragment?.fastUpdateGoals()
    }
}
