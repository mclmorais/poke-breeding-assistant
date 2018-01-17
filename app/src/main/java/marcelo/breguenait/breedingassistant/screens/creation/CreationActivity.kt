package marcelo.breguenait.breedingassistant.screens.creation

import android.net.Uri
import android.os.Bundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.creation_activity.*
import marcelo.breguenait.breedingassistant.R

class CreationActivity : AppCompatActivity(), CreationFragment.OnFragmentInteractionListener {


    private var creationFragment: CreationFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creation_activity)

        /*Sets support action bar for activity*/
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*Changes indicator to the 'clear' cross and tints it white*/
        val indicator =
            VectorDrawableCompat.create(resources, R.drawable.ic_clear_black_24dp, theme)
        indicator?.setTint(ResourcesCompat.getColor(resources, R.color.white, theme))
        supportActionBar?.setHomeAsUpIndicator(indicator)

        creationFragment =
                supportFragmentManager.findFragmentById(R.id.content_frame) as CreationFragment?
        if (creationFragment == null) {
            creationFragment = CreationFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.content_frame, creationFragment)
                .commit()
        }
    }


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
