package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_full_screen_image.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.*

class FullScreenImageActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var image_uri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        intent = getIntent()

        image_uri = intent.getStringExtra("image_uri");

        if (intent != null && image_uri != null) {
            ImageLoader.getInstance().displayImage(image_uri, imageIV, Utils.UILoptions);
        }

//        finishLL.setOnClickListener { finish() }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

}
