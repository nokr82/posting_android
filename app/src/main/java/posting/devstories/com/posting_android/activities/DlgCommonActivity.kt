package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dlg_common.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class DlgCommonActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var contents = ""
    var cancel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_common)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        contents = intent.getStringExtra("contents")
        cancel = intent.getBooleanExtra("cancel", false)

        if(cancel) {
            cancelTV.visibility = View.VISIBLE
        } else {
            cancelTV.visibility = View.GONE
        }

        contentsTV.text = contents

        cancelTV.setOnClickListener {
            var intent = Intent();
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        doneTV.setOnClickListener {
            var intent = Intent();
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    private fun stopDlg() {

        finish()

    }


}
