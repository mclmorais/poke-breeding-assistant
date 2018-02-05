package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View

import java.util.ArrayList

import javax.inject.Inject

import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.application.BreedingAssistantApplication
import marcelo.breguenait.breedingassistant.screens.assistant.injection.DaggerAssistantComponent

class AssistantActivity : AppCompatActivity() {


    @Inject
    lateinit var assistantPresenter: AssistantPresenter

    private lateinit var assistantFragment: AssistantFragment

    private lateinit var storedPokemonFragment: StoredPokemonFragment

    fun getAssistantPresenter(): AssistantContract.Presenter {
        return assistantPresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.assistant_activity)


        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        assistantFragment = AssistantFragment()
        storedPokemonFragment = StoredPokemonFragment()

        val viewPager = findViewById<ViewPager>(R.id.assistant_viewpager)
        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(assistantFragment, getString(R.string.label_assistant))
        adapter.addFragment(storedPokemonFragment, getString(R.string.label_storage))
        viewPager.adapter = adapter

        val tabs = findViewById<TabLayout>(R.id.assistant_tabs)
        tabs.setupWithViewPager(viewPager)

        DaggerAssistantComponent
            .builder()
            .applicationComponent(BreedingAssistantApplication.get(this).component)
            .build()
            .inject(this)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_assistant, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home     -> {
                onBackPressed()
                return true
            }
            R.id.action_edit_goal -> assistantFragment.requestGoalEdit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("ASD", 3)
        setResult(333, data)
        supportFinishAfterTransition()
    }

    private class Adapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()

        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }


}




