package dk.nodes.template.presentation.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import dk.nodes.template.domain.entities.TabbarType
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.ui.base.BaseActivity
import dk.nodes.template.presentation.ui.history.HistoryFragment
import dk.nodes.template.presentation.ui.home.HomeFragment
import dk.nodes.template.presentation.ui.loadmodel.LoadModelFragment
import dk.nodes.template.presentation.ui.main.database.VectorBaseHelper
import dk.nodes.template.presentation.ui.negative.NegativeListFragment
import dk.nodes.template.presentation.ui.positive.PositiveListFragment
import dk.nodes.template.presentation.ui.results.ResultsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_tabbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class MainActivity : BaseActivity(R.layout.activity_main) {

    private val viewModel by viewModel<MainActivityViewModel>()
    private var selectedTab = TabbarType.PICTURES


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForPermissions()
        setContentView(R.layout.activity_main)
        setupSliderMenu()
        Timber.plant(Timber.DebugTree())
        setupListeners()
        tabbarSetup()
        viewModel.getLabelsFromAssets(this)
        viewModel.generateModels()
    }


    private fun setupListeners() {
        burgerMenu?.setOnClickListener {
            burgerMenu?.setImageResource(R.drawable.ic_icon_m_menu_blue)
            sliderClicked()

        }
        presstoStartOver?.setOnClickListener {
            presstoStartOver?.setImageResource(R.drawable.ic_refresh_blue)
            viewModel.deleteCandidates()
            viewModel.emptyLists()
            debounceAndHome()
        }
    }

    fun setGenerateModelsRunning(boolean: Boolean) {
        viewModel.setGenerateModelAlreadyRunning(boolean)
    }

    fun generateModels() {
        resultsTab?.isEnabled = false
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.deleteCandidates()
            viewModel.generateModels()
            delay(440)
            resultsTab?.isEnabled = true
        }
    }

    fun deleteCandidates() {
        viewModel.emptyLists()
        viewModel.deleteCandidates()
    }


    private fun sliderClicked() {
        slideNavigation?.openDrawer(GravityCompat.START)
        burgerMenu?.setImageResource(R.drawable.ic_icon_m_menu_dark_grey)
    }

    private fun debounceAndHome() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(300)
            setTabsUnselected()
            setTabSelected(TabbarType.PICTURES)
            picturesLogo?.setImageResource(R.drawable.ic_image_pictures_blue)
            presstoStartOver?.setImageResource(R.drawable.ic_refresh_darkgrey)
            setFragment(HistoryFragment())
        }
    }

    fun animateAddOneToPositive() {
        positiveLogo?.setImageResource(R.drawable.ic_icon_l_plus_green)
    }

    fun animatePositiveBackToNormal() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(300)
            positiveLogo?.setImageResource(R.drawable.ic_icon_l_plus_bluegrey)
        }
    }

    fun animateAddOneToNegative() {
        negativeLogo?.setImageResource(R.drawable.ic_icon_l_delete_red)
    }

    fun animateNegativeBackToNormal() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(300)
            negativeLogo?.setImageResource(R.drawable.ic_action_minus_bluegrey)
        }
    }

    fun gotoHistory() {
        setTabsUnselected()
        setTabSelected(TabbarType.PICTURES)
        setFragment(HistoryFragment())
        picturesLogo?.setImageResource(R.drawable.ic_image_pictures_blue)
    }

    private fun tabbarSetup() {
        setTabSelected(TabbarType.PICTURES)
        picturesLogo?.setImageResource(R.drawable.ic_image_pictures_blue)

        loadModelTab?.setOnClickListener {

            setTabsUnselected()
            setTabSelected(TabbarType.SAVEDMODELS)
            setFragment(LoadModelFragment())
            savedModelLogo?.setImageResource(R.drawable.ic_saved_blue)

        }

        positiveTab?.setOnClickListener {
            setFragment(PositiveListFragment())
            setTabsUnselected()
            setTabSelected(TabbarType.POSITIVE)
            positiveLogo.setImageResource(R.drawable.ic_icon_l_plus_blue)
        }

        negatativeTab?.setOnClickListener {
            setFragment(NegativeListFragment())
            setTabsUnselected()
            setTabSelected(TabbarType.NEGATIVE)
            negativeLogo.setImageResource(R.drawable.ic_action_minus_blue)
        }

        resultsTab?.setOnClickListener {
            gotoResults()
        }

        picturesTab?.setOnClickListener {
            gotoHistory()
        }
    }

    private fun gotoResults() {
        setTabsUnselected()
        setTabSelected(TabbarType.RESULTS)
        resultsLogo.setImageResource(R.drawable.ic_icon_m_search_blue)
        setFragment(ResultsFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setTabSelected(type: TabbarType) {
        selectedTab = type
        val view = when (type) {
            TabbarType.POSITIVE -> {
                positiveTab
            }
            TabbarType.NEGATIVE -> {
                negatativeTab
            }
            TabbarType.RESULTS -> {
                resultsTab
            }
            TabbarType.PICTURES -> {
                picturesTab
            }
            TabbarType.SAVEDMODELS -> {
                loadModelTab
            }
        }
        view.isSelected = true
    }

    private fun setTabsUnselected() {
        positiveTab.isSelected = false
        loadModelTab.isSelected = false
        negatativeTab.isSelected = false
        resultsTab.isSelected = false
        picturesTab.isSelected = false
        savedModelLogo?.setImageResource(R.drawable.ic_saved_bluegrey)
        positiveLogo?.setImageResource(R.drawable.ic_icon_l_plus_bluegrey)
        negativeLogo?.setImageResource(R.drawable.ic_action_minus_bluegrey)
        resultsLogo?.setImageResource(R.drawable.ic_icon_m_search_bluegrey)
        presstoStartOver?.setImageResource(R.drawable.ic_refresh_darkgrey)
        picturesLogo?.setImageResource(R.drawable.ic_image_pictures_bluegrey)
    }

    private fun checkForPermissions() {
        if (!allPermissionsGranted()) {
            requestPermissions()
            checkForPermissions()
        } else {
            setupPathDir()
            setFragment(HistoryFragment())
        }
    }

    private fun setupPathDir() {
        val backUpDir = File(Environment.getExternalStorageDirectory(), "DBBackup2")
        val backupDB2 = File(backUpDir, "vectorDB.db")
        if (backupDB2.exists()) {
            // Creating internal storage databases folder
            var internalPath = ""
            internalPath = this.applicationInfo.dataDir + "/databases/"
            val internalDir = File(internalPath)
            internalDir.mkdir()
            VectorBaseHelper.importDB(backupDB2, internalPath, this)
        }
//        else {
//            val serverClient = ServerClient()
//            serverClient.connectServer(context, allImages)
//        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
    }

    private fun allPermissionsGranted(): Boolean {

        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )

        var context: Context? = null

    }

    override fun onBackPressed() {
        returnToFragmentOnBackPressed()
    }

    private fun returnToFragmentOnBackPressed() {
        val type = selectedTab
        when (type) {
            TabbarType.RESULTS -> gotoResults()
            TabbarType.PICTURES -> gotoHistory()
            TabbarType.NEGATIVE -> setFragment(NegativeListFragment())
            TabbarType.POSITIVE -> setFragment(PositiveListFragment())
            else -> gotoHistory()
        }
    }
}

