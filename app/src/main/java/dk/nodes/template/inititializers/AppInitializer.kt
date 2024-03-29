package dk.nodes.template.inititializers

import android.app.Application
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerCollector
import dk.nodes.nstack.kotlin.NStack
import dk.nodes.template.data.BuildConfig
import dk.nodes.template.domain.managers.ThemeManager
import dk.nodes.template.presentation.util.ThemeHelper
import timber.log.Timber
import javax.inject.Inject

interface AppInitializer {
    fun init(app: Application)
}

class AppInitializerImpl @Inject constructor(
    private val themeManager: ThemeManager,
    private val chuckerCollector: ChuckerCollector
) : AppInitializer {
    override fun init(app: Application) {
        if (BuildConfig.DEBUG) {
            NStack.enableLiveEdit(app)
            Timber.plant(Timber.DebugTree(), chuckerTree())
        }
        ThemeHelper.applyTheme(themeManager.theme)
    }

    private fun chuckerTree() = object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority >= Log.ERROR) {
                chuckerCollector.onError(
                    tag ?: BuildConfig.LIBRARY_PACKAGE_NAME,
                    t ?: Throwable("Unknown Error")
                )
            }
        }
    }

}