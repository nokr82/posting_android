package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_postchat.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ChatAdapter
import posting.devstories.com.posting_android.adapter.MainAdapter
import posting.devstories.com.posting_android.base.RootActivity
import java.util.ArrayList

class PostChatActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false
    var mainAdapterData = ArrayList<JSONObject>();
    lateinit var ChatAdapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postchat)


        val item = Array(20,{ "안녕" })

        chatLV.adapter = ChatAdapter(this,item)







        this.context = this
        progressDialog = ProgressDialog(context)

    }

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
