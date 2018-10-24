package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_mypage.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class MyPageActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        this.context = this
        progressDialog = ProgressDialog(context)



        finishLL.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
