package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.dlg_chatting_exit.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class DlgYesOrNoCommonActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var contents = ""
    var cancel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_chatting_exit)

        this.context = this
        progressDialog = ProgressDialog(context)

        contentsTV.text = intent.getStringExtra("contents")

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

}
