package dk.nodes.template.presentation.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.core.view.GravityCompat
import dk.nodes.template.domain.entities.MenuType
import dk.nodes.template.presentation.BuildConfig
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.ui.info.InfoActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_menu.view.*
import kotlinx.android.synthetic.main.navigation_menu.*
import timber.log.Timber


fun MainActivity.setupSliderMenu() {
    setMenuItem(
        menuAbout,
        "How to use this App",
        R.drawable.ic_icon_m_circle_light_grey,
        MenuType.ABOUT
    )
    setMenuItem(
        menuNiceToKnow,
        "How to store a model",
        R.drawable.ic_icon_m_circle_light_grey,
        MenuType.NICETOKNOW
    )
    setMenuItem(
        menuTerms,
        "How to load a stored model",
        R.drawable.ic_icon_m_circle_light_grey,
        MenuType.TERMS
    )
}


private fun MainActivity.setMenuItem(view: View, title: String, logo: Int, menuType: MenuType) {
    view.menuItemText?.text = title
    view.menuItemLogo?.setImageResource(logo)
    view.setOnClickListener {
        when (menuType) {

            MenuType.ABOUT -> startInfoActivity(menuType)
            MenuType.NICETOKNOW -> startInfoActivity(menuType)
            MenuType.TERMS -> startInfoActivity(menuType)
        }
    }
}


private fun MainActivity.startInfoActivity(menuType: MenuType) {
    slideNavigation?.closeDrawer(GravityCompat.START)
    val intent = Intent(this, InfoActivity::class.java)
    intent.putExtra("type", menuType)
    startActivity(intent)
    this.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_left)
}



