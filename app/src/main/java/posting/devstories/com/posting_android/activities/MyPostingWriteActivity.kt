package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_posttextwrite.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class MyPostingWriteActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false

    var mee = arrayOf("Metting")
    var most =  arrayOf("수량")

    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posttextwrite)

        this.context = this
        progressDialog = ProgressDialog(context)



        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mee)
        meetingSP.adapter = adpater



        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,most)
        mostSP.adapter = adpater




    }

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
