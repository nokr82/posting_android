package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.join_dlg.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class DlgJoinActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var type:String = ""
    var message:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_dlg)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        type = intent.getStringExtra("type")

        if(type == "join_ok") {
            PostingStartTX.text = "POSTING 시작하기"
        } else {

            message = intent.getStringExtra("message")

            PostingStartTX.text = "회원정보 입력하기"
            messageTV.text = message
        }

        PostingStartTX.setOnClickListener {
            var intent = Intent()
            setResult(Activity.RESULT_OK, intent)

            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
