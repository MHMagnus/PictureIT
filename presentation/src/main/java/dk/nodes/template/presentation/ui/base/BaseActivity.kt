package dk.nodes.template.presentation.ui.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.extensions.getViewModel
import dk.nodes.template.presentation.extensions.lifecycleAwareLazy
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity, HasAndroidInjector {

    constructor(@LayoutRes resId: Int) : super(resId)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }


    protected inline fun <reified VM : ViewModel> getViewModel(): VM =
        getViewModel(viewModelFactory)

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> {
        return lifecycleAwareLazy(this) { getViewModel<VM>() }
    }


    fun setFragment(fragment: Fragment, overrideBackStack: Boolean = true) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in, android.R.animator.fade_out,
                android.R.animator.fade_in, android.R.animator.fade_out
        )

        fragmentTransaction.replace(
                R.id.main_container,
                fragment,
                fragment.javaClass.simpleName
        )

        if (overrideBackStack) {
            supportFragmentManager.popBackStack(
                    fragment::class.java.simpleName,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else {
            fragmentTransaction.addToBackStack(fragment::class.java.simpleName)
        }

        fragmentTransaction.commit()
    }


    override fun androidInjector() = androidInjector
}
