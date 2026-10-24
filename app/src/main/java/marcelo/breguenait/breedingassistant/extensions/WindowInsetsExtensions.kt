package marcelo.breguenait.breedingassistant.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/** Edge-to-edge is mandatory from API 35: keeps app bar content below the status bar. */
fun View.padTopForStatusBar() {
    val initialTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updatePadding(top = initialTop + insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
        insets
    }
}
