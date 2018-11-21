package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.myposting_dlg.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class DlgMyCommentsActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var comments_id = -1
    var comments = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myposting_dlg)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        comments_id = intent.getIntExtra("comments_id", -1)
        comments = intent.getStringExtra("comments")

        recyTV.visibility = View.GONE
        titleTV.text = "My Comments"

        delTV.setOnClickListener {
            setResult("del")
        }
        modiTV.setOnClickListener {
            setResult("edit")
        }

    }

    fun setResult(type:String){
        var intent = Intent()
        intent.putExtra("comments_id", comments_id)
        intent.putExtra("comments", comments)
        intent.putExtra("type", type)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
