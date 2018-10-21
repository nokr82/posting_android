package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_posttextwrite.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class MyPostingWriteActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null

    var imgid:String? = null
    var mee = arrayOf("Metting")
    var most =  arrayOf("수량")

    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posttextwrite)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        imgid = intent.getStringExtra("imgid")

        //이미지
        img2RL.background = Drawable.createFromPath(imgid)
        if (imgid !=null){
            popupRL.visibility = View.VISIBLE
        }


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
