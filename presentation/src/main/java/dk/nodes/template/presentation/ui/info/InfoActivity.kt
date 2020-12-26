package dk.nodes.template.presentation.ui.info

import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import dk.nodes.template.domain.entities.InfoItem
import dk.nodes.template.domain.entities.MenuType
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.extensions.observeNonNull
import dk.nodes.template.presentation.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_info.*


class InfoActivity : BaseActivity(R.layout.activity_info) {

    private val viewModel by viewModel<InfoActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        viewModel.viewState.observeNonNull(this) { state ->
            state.data?.let { handleData(it) }
        }
        val type = intent?.extras?.get("type") as? MenuType
        viewModel.loadInfo(type)

        infoCloseIv?.setOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right)
    }

    private fun showError() {
        Toast.makeText(this, "Something Went Wrong Trying To Open The Menu", Toast.LENGTH_SHORT).show()
    }

    private fun handleData(infoItem: InfoItem?) {
        val data = infoItem ?: return
        infoTitle.text = "${data.title}"
        infoContent.text = "${data.content}"

        Glide.with(infoImageOne)
                .load(data.imageOne)
                .into(infoImageOne)
        Glide.with(infoImageTwo)
                .load(data.imageTwo)
                .into(infoImageTwo)
        Glide.with(infoImageThree)
                .load(data.imageThree)
                .into(infoImageThree)
    }

}