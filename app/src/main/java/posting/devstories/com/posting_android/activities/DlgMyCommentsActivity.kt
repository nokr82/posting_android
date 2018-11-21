package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.myposting_dlg.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class DlgMyCommentsActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var comments_id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myposting_dlg)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        comments_id = intent.getIntExtra("comments_id", -1)

        recyTV.visibility = View.GONE
        titleTV.text = "My Comments"

        delTV.setOnClickListener {
            delComments()
        }
        modiTV.setOnClickListener {
            editComments()
        }

    }

    fun editComments(){

    }

    fun delComments(){

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
