package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_guide.*
import posting.devstories.com.posting_android.adapter.GuideAdapter

class GuideActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var adverImagePaths = ArrayList<String>()
    var adPosition = 0
    private lateinit var guideAdapter: GuideAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        adverImagePaths.add("1")
        adverImagePaths.add("2")
        adverImagePaths.add("3")
        adverImagePaths.add("4")
        adverImagePaths.add("5")
        adverImagePaths.add("6")
        adverImagePaths.add("7")
        adverImagePaths.add("8")
        adverImagePaths.add("9")
        adverImagePaths.add("10")
        adverImagePaths.add("11")
        adverImagePaths.add("12")
        guideAdapter = GuideAdapter(this, adverImagePaths)
        guideVP.adapter = guideAdapter
        guideVP.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                adPosition = position
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {

            }
        })



    }



    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

}
