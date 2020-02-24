package hu.ppke.itk.mindtechpokemon.basic

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.encosoft.fuger.ui.common.SharedTransition
import java.lang.ref.WeakReference
import java.util.*

abstract class NavViewModel : ViewModel(), NavController.OnDestinationChangedListener {
    private val navigationQueue: Queue<NavDirections> = ArrayDeque(1)
    private val navigation = MutableLiveData<NavDirections>()
    private val TAG = "Navigation"
    
    protected fun navigateTo(direction: NavDirections, justQueue: Boolean = false) {
        if (justQueue || navigation.value != null) {
            navigationQueue.add(direction)
        } else {
            navigation.postValue(direction)
        }
    }

    protected fun clearNavigationQueue() {
        navigationQueue.clear()
        navigation.postValue(null)
    }

    fun setupNavigation(fragment: Fragment) {
        try {
            fragment.findNavController().addOnDestinationChangedListener(this)
        } catch (e: Exception) {
            Log.e(TAG, "Navigation listener error!", e)
        }

        navigation.observe(fragment.viewLifecycleOwner, Observer { direction ->
            try {
                doNavigate(fragment.findNavController(), direction, fragment.navigationExtras)
            } catch (e: Exception) {
                Log.w(TAG, "Navigation error!", e)
            }
        })
    }

    fun setupNavigation(lifecycleOwner: LifecycleOwner, findController: (LifecycleOwner) -> NavController) {
        try {
            findController(lifecycleOwner).addOnDestinationChangedListener(this)
        } catch (e: Exception) {
            Log.e(TAG, "Navigation listener error!", e)
        }

        val reference = WeakReference(lifecycleOwner)
        navigation.observe(lifecycleOwner, Observer { direction ->
            reference.get()?.let { owner ->
                try {
                    doNavigate(findController(owner), direction, owner.navigationExtras)
                } catch (e: Exception) {
                    Log.w(TAG, "Navigation error!", e)
                }
            }
        })
    }

    private fun doNavigate(controller: NavController, direction: NavDirections?, extras: FragmentNavigator.Extras?) {
        if (direction != null) {
            controller.navigate(direction.actionId, direction.arguments, null, extras)
            navigation.value = null
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (navigationQueue.isNotEmpty()) navigation.postValue(navigationQueue.poll())
    }

    private val LifecycleOwner.navigationExtras get() = (this as? SharedTransition)?.transitionExtras

}