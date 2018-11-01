package posting.devstories.com.posting_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.Actions.PostingAction.detail
import posting.devstories.com.posting_android.Actions.PostingAction.save_posting
import posting.devstories.com.posting_android.Actions.PostingAction.write_comments
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.adapter.ReAdapter
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var member_id = -1
    var posting_save_id = ""
    var posting_id  = ""
    var p_comments_id = -1
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    var count = 0
    var del_yn = ""
    var use_yn:String?= null
    var image_uri = ""
    var type = 1
    var contents = ""
    var coupon = -1
    lateinit var adapterRe: ReAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()


        coupon = intent.getIntExtra("coupon",-1)
        use_yn = intent.getStringExtra("use_yn")
        println("=============쿠폰사용"+use_yn)

        posting_id = intent.getStringExtra("id")


        member_id = PrefUtils.getIntPreference(context, "member_id")
        commentsLV.isExpanded = true
        adapterRe = ReAdapter(context,R.layout.item_re, adapterData)
        commentsLV.adapter = adapterRe
        adapterRe.notifyDataSetChanged()

        commentsLV.setOnItemClickListener { adapterView, view, i, l ->

            var data = adapterData.get(i)
            val comments_id = Utils.getInt(data, "id")
            if (comments_id != -1) {
                p_comments_id = comments_id
                commentsET.requestFocus()
                Utils.showKeyboard(context)
                commentsET.hint = "답글쓰기"
            }
        }

        couponTV.setOnClickListener {
            coupondlgView()
        }


        menuIV.setOnClickListener {
            dlgView()
        }

        commentsET.setOnEditorActionListener { textView, i, keyEvent ->
            commentsET.hint = "댓글쓰기"
            when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    var comments = Utils.getString(commentsET)

                    if(!comments.isEmpty() && comments != "") {
                        writeComments(comments)

                    }
                }
            }
            return@setOnEditorActionListener true

        }
        postingLL.setOnClickListener {

            if(count < 1) {

                Toast.makeText(context, "남은 포스팅 갯수가 없습니다.", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            savePosting();

        }

        backLL.setOnClickListener {
            finish()
        }

        detaildata()

    }

    fun writeComments(comments:String) {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)
        params.put("comments", comments)
        params.put("p_comments_id", p_comments_id)

        write_comments(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {
                        detaildata()
                        Utils.hideKeyboard(context)
                        commentsET.setText("")



                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun use_posting(){
        val params = RequestParams()
        params.put("posting_save_id", posting_save_id)

        PostingAction.use_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {


                        finish()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    fun savePosting() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)

        save_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var intent = Intent(context, DlgStorageActivity::class.java)
                        startActivity(intent)


                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.action = "SAVE_POSTING"
                        sendBroadcast(intent)

                    }else if ("empty"==result){
                        Toast.makeText(context,"남은 수량이 없습니다.",Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun detaildata() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)
        params.put("del_yn", del_yn)

        detail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("posting")


                        val posting = data.getJSONObject("Posting")

                        posting_save_id  = Utils.getString(posting,"posting_save_id")
                        println("posting============"+posting)
                        val save_yn = Utils.getString(posting,"save_yn")
                        val use_yn = Utils.getString(posting,"use_yn")


                        var uses_start_date = Utils.getString(posting, "uses_start_date")
                        var uses_end_date =   Utils.getString(posting, "uses_end_date")




                        var id = Utils.getString(posting, "id")
                        var member_id2 =   Utils.getInt(posting, "member_id")

                        var del = Utils.getString(posting,"del_yn")
                        var Image = Utils.getString(posting, "Image")
                        type = Utils.getInt(posting,"type")


                        if (type ==6&&use_yn.equals("Y")){
                            couponLL.visibility = View.GONE
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }else if(type ==6&&save_yn.equals("Y")&&use_yn.equals("N")){
                            couponLL.visibility = View.VISIBLE
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }


                       image_uri = Utils.getString(posting, "image_uri")
                        count = Utils.getInt(posting, "leftCount")
//                        var created =   Utils.getString(posting, "created")
                        if (member_id==member_id2){
                            myLL.visibility = View.VISIBLE
                            menuIV.visibility = View.VISIBLE
                        }

                        val member  = data.getJSONObject("Member")

                        contents =   Utils.getString(posting, "contents")
                        var nick_name = Utils.getString(member, "nick_name")


                        val data2 = response.getJSONObject("posting")
                        println("===================="+data)
                        val comments = data2.getJSONArray("PostingComment")
                        p_comments_id = -1
                        adapterData.clear()
                        for (i in 0..comments.length() - 1) {

                            println("data[i] : " + comments[i])

                            adapterData.add(comments[i] as JSONObject)

                        }


                        adapterRe.notifyDataSetChanged()


                        contentTV.text = contents
                        wnameTX.text = nick_name

                        val sdf = SimpleDateFormat("MM월dd일", Locale.KOREA)
                        val created = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utils.getString(posting, "created"))
                        val create_date = sdf.format(created)

                        upTX.text = create_date

                        //uri를 이미지로 변환시켜준다
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            ImageLoader.getInstance().displayImage(image, imgIV, Utils.UILoptionsUserProfile)
                            imgIV.visibility = View.VISIBLE
                        } else {
                            contentsTV.text = contents
                            contentsTV.visibility = View.VISIBLE
                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }


    fun coupondlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.coupon_dlg, null)
        val couponnoTX = dialogView.findViewById<TextView>(R.id.couponnoTX)
        val couponyTX = dialogView.findViewById<TextView>(R.id.couponyTX)


        couponnoTX.setOnClickListener {
            mPopupDlg!!.cancel()
        }

        couponyTX.setOnClickListener {

            use_posting()
            mPopupDlg!!.cancel()

        }

        mPopupDlg =  builder.setView(dialogView).show()

    }

    fun dlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
        val secretTV = dialogView.findViewById<TextView>(R.id.secretTV)
        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)

        delTV.setOnClickListener {
            del_posting()
            mPopupDlg!!.cancel()
        }

        modiTV.setOnClickListener {

            val intent = Intent(context, PostWriteActivity::class.java)
            intent.putExtra("posting_id", posting_id)
            intent.putExtra("image_uri",image_uri)
            intent.putExtra("contents",contents)

            context.startActivity(intent)
            finish()

            mPopupDlg!!.cancel()

        }

        secretTV.setOnClickListener {

        }

        recyTV.setOnClickListener {

        }


        mPopupDlg =  builder.setView(dialogView).show()

    }

    fun del_posting(){
        val params = RequestParams()
        params.put("posting_id", posting_id)

    PostingAction.del_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.putExtra("type", type)
                        intent.action = "DEL_POSTING"
                        sendBroadcast(intent)

                        finish()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
