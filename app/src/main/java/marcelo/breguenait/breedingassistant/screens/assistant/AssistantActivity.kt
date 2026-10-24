package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.transition.doOnEnd
import androidx.core.view.doOnPreDraw
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.extensions.padTopForStatusBar
import marcelo.breguenait.breedingassistant.databinding.AssistantActivityBinding
import marcelo.breguenait.breedingassistant.application.BreedingAssistantApplication
import marcelo.breguenait.breedingassistant.screens.assistant.injection.DaggerAssistantComponent
import javax.inject.Inject


class AssistantActivity : AppCompatActivity(), StoredPokemonFragment.PresenterCallback {


    @Inject
    lateinit var assistantPresenter: AssistantPresenter

    internal lateinit var binding: AssistantActivityBinding

    private lateinit var assistantFragment: AssistantFragment

    private lateinit var storedPokemonFragment: StoredPokemonFragment

    fun getAssistantPresenter(): AssistantContract.Presenter {
        return assistantPresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AssistantActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appbar.padTopForStatusBar()

        onBackPressedDispatcher.addCallback(this) {
            setResult(333, Intent().putExtra("ASD", 3))
            supportFinishAfterTransition()
        }


        // Postpone the transition until the window's decor view has
        // finished its layout.
        postponeEnterTransition()

        window.decorView.doOnPreDraw {
            startPostponedEnterTransition()
        }


        window.sharedElementEnterTransition.doOnEnd {
            binding.visibilityGroup.visibility = View.VISIBLE
            binding.toolbar.visibility = View.VISIBLE
        }


        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        assistantFragment = AssistantFragment()
        storedPokemonFragment = StoredPokemonFragment.newInstance()

        val pages = listOf(
            assistantFragment to getString(R.string.label_assistant),
            storedPokemonFragment to getString(R.string.label_storage))
        binding.assistantViewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = pages.size
            override fun createFragment(position: Int) = pages[position].first
        }
        // AssistantPresenter needs both tab views registered up front, as the old ViewPager did
        binding.assistantViewpager.offscreenPageLimit = 1
        TabLayoutMediator(binding.assistantTabs, binding.assistantViewpager) { tab, position ->
            tab.text = pages[position].second
        }.attach()

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
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.action_edit_goal -> assistantFragment.requestGoalEdit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setPresenter(): BoxContract.Presenter {
        return assistantPresenter
    }



}




