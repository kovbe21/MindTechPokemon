package hu.ppke.itk.mindtechpokemon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController

class MainActivity :  NavController.OnDestinationChangedListener, AppCompatActivity() {

    private val navController get() = findNavController(R.id.activity_main_nav_host_fragment)

    private var destinationArgs: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDefaultArgs(savedInstanceState)
    }

    private fun setDefaultArgs(extras: Bundle?) {
        navController.setGraph(R.navigation.main_graph, extras)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(Constants.KEY_NAV_ARGS, destinationArgs)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        destinationArgs = savedInstanceState.getBundle(Constants.KEY_NAV_ARGS)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // show / hide toolbar based on navigation args
        Log.d("DESTINATIONS", "destination changed post")
        destinationArgs = arguments
    }

}
