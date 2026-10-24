package marcelo.breguenait.breedingassistant.extensions

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.inTransaction(body: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        body()
        commit()
    }

}
    fun FragmentManager.inAnimatedTransaction(body: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            body()
            commit()
        }
    }
