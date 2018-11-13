package posting.devstories.com.posting_android.activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_chatting.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.ChattingAction
import posting.devstories.com.posting_android.Actions.ReviewAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ChattingAdapter
import posting.devstories.com.posting_android.base.BackPressCloseHandler
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import java.io.ByteArrayInputStream
import java.util.*

/**
 * Created by dev1 on 2018-02-28.
 */

class ChattingActivity : RootActivity(), AbsListView.OnScrollListener {

    private var userScrolled: Boolean = false
    private var lastItemVisibleFlag: Boolean = false

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val backPressCloseHandler: BackPressCloseHandler? = null

    var attend_member_id = -1
    var chatting_group_id = -1
    var first_id = -1
    var last_id = -1

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    private lateinit var adapter: ChattingAdapter

    internal var loadDataHandler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            chatting()
        }
    }

    private var timer: Timer? = null

    var member_id = -1

    private val FROM_ALBUM = 101
    private val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2
    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        this.context = this
        progressDialog = ProgressDialog(context)

        member_id = PrefUtils.getIntPreference(context, "member_id")

        attend_member_id = intent.getIntExtra("attend_member_id", -1)

        backLL.setOnClickListener {
            finish()
        }

        adapter = ChattingAdapter(this, R.layout.item_chat, adapterData)
        chatLV.adapter = adapter
        chatLV.setOnScrollListener(this)

//        messageET.setOnEditorActionListener { v, actionId, event ->
//
//            when(actionId) {
//                EditorInfo.IME_ACTION_SEND -> {
//
//                    val message = Utils.getString(messageET)
//
//                    if(message == "" || message.isEmpty()) {
//                        return@setOnEditorActionListener true
//                    }
//
//                    if(emptyLL.visibility == View.VISIBLE) {
//                        chattingAdd("t", message)
//                    } else {
//                        sendMessage("t", message)
//                    }
//
//                }
//            }
//
//            return@setOnEditorActionListener true
//        }

        submitTV.setOnClickListener {

            val message = Utils.getString(messageET)

            if(message == "" || message.isEmpty()) {
                return@setOnClickListener
            }

            if(emptyLL.visibility == View.VISIBLE) {
                chattingAdd("t", message)
            } else {
                sendMessage("t", message)
            }

        }

        reportLL.setOnClickListener {
            policedlgView()
        }

        plusLL.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                loadPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
            } else {
                imageFromGallery()
            }

        }

        chattingCheck()

    }

    private fun loadPermissions(perm: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, perm) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm), requestCode)
        } else {
            imageFromGallery()
        }
    }

    private fun imageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, FROM_ALBUM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromGallery()
                } else {
                    // no granted
                }
                return
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                FROM_ALBUM -> if (data != null && data.data != null) {
                    val selectedImageUri = data.data

                    val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)

                    val cursor =
                        context!!.contentResolver.query(selectedImageUri!!, filePathColumn, null, null, null)
                    if (cursor!!.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath = cursor.getString(columnIndex)

                        cursor.close()

                        selectedImage = Utils.getImage(context!!.contentResolver, picturePath)

                        sendMessage("i", "")

                    }
                }
            }
        }
    }

    fun policedlgView(){


        var intent = Intent(context, DlgReportActivity::class.java)
        intent.putExtra("dlgtype", "police_member")
        intent.putExtra("report_member_id", attend_member_id)
        startActivity(intent)


//        var mPopupDlg: DialogInterface? = null
//
//        val builder = AlertDialog.Builder(this)
//        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
//        val titleTV = dialogView.findViewById<TextView>(R.id.titleTV)
//        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
//        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
//        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)
//        titleTV.text = "이 사용자를 신고하는 이유를 선택하세요"
//        delTV.text = "불건전합니다"
//        modiTV.text = "부적절합니다"
//        recyTV.text = "스팸입니다"
//
//        mPopupDlg =  builder.setView(dialogView).show()
//
//        delTV.setOnClickListener {
//            report("1")
//            mPopupDlg.dismiss()
//
//        }
//        modiTV.setOnClickListener {
//            report("2")
//            mPopupDlg.dismiss()
//        }
//        recyTV.setOnClickListener {
//            report("3")
//            mPopupDlg.dismiss()
//        }

    }

    fun report(type:String){
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("report_member_id", attend_member_id)
        params.put("type", type)

        ReviewAction.report(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        var intent = Intent(context, DlgPoliceActivity::class.java)
                        startActivity(intent)

                    } else if("already" == result) {
                        Toast.makeText(context, "신고한 회원입니다.", Toast.LENGTH_LONG).show()
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

    fun chattingCheck() {
        val params = RequestParams()
        params.put("founder_member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("attend_member_id", attend_member_id)

        ChattingAction.chattingCheck(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result || "empty" == result) {

                        val att_member = response.getJSONObject("att_member")

                        nickNameTV.text = Utils.getString(att_member, "nick_name")

                        var profile_uri = Config.url + Utils.getString(att_member,"image_uri")
                        ImageLoader.getInstance().displayImage(profile_uri, profileIV, Utils.UILoptionsProfile)

                        nickName2TV.text = Utils.getString(att_member, "nick_name")

                        ImageLoader.getInstance().displayImage(profile_uri, profile2IV, Utils.UILoptionsProfile)

                        if("ok" == result) {

                            chatting_group_id = Utils.getInt(response, "chatting_group_id")

                            chatLV.visibility = View.VISIBLE

                            timerStart()

                        } else {
                            emptyLL.visibility = View.VISIBLE
                        }

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

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

    fun timerStart(){
        val task = object : TimerTask() {
            override fun run() {
                loadDataHandler.sendEmptyMessage(0)
            }
        }

        timer = Timer()
        timer!!.schedule(task, 0, 2000)

        chatLV.setSelection(adapter.count - 1)
    }

    fun chatting() {

        if (first_id < 1) {
            if (adapterData.size > 0) {
                try {
                    try {
                        val lastMSG = adapterData.get(adapterData.size - 1)
                        val chatting = lastMSG.getJSONObject("Chatting")
                        last_id = Utils.getInt(chatting, "id")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } catch (e: NumberFormatException) {

                }

            }
        }

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("first_id", first_id)
        params.put("last_id", last_id)
        params.put("chatting_group_id", chatting_group_id)

        ChattingAction.chatting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val list = response.getJSONArray("list")

                        if (first_id > 0) {
                            for (i in 0 until list.length()) {
                                val data = list.get(i) as JSONObject
                                adapterData.add(0, data)
                            }

                        } else {
                            for (i in 0 until list.length()) {
                                val data = list.get(i) as JSONObject

                                adapterData.add(data)
                            }
                        }

                        if (adapterData.size > 0) {
                            val data = adapterData[adapterData.size - 1]
                            val chatting = data.getJSONObject("Chatting")
                            last_id = Utils.getInt(chatting, "id")
                        }

                        if (list.length() > 0) {
                            (adapter as BaseAdapter).notifyDataSetChanged()
                        }

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }


            override fun onStart() {
                // show dialog
//                if (progressDialog != null) {
//                    progressDialog!!.show()
//                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun sendMessage(type: String, message: String) {

        val params = RequestParams()
        params.put("chatting_group_id", chatting_group_id)
        params.put("send_member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("type", type)

        if(type == "t") {
            params.put("message", message)
        } else {
            // 이미지
            if (selectedImage != null) {
                val selectedImg = ByteArrayInputStream(Utils.getByteArray(selectedImage))
                params.put("upload", selectedImg)
            }
        }

        ChattingAction.sendMessage(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    messageET.setText("")
                    selectedImage = null

                    if ("ok" == result) {
                    } else {
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

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

    fun chattingAdd(type: String, message: String) {

        val params = RequestParams()
        params.put("founder_member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("attend_member_id", attend_member_id)
        params.put("type", type)

        if(type == "t") {
            params.put("message", message)
        } else {
            if (selectedImage != null) {
                val selectedImg = ByteArrayInputStream(Utils.getByteArray(selectedImage))
                params.put("upload", selectedImg)
            }
        }

        ChattingAction.chattingAdd(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        chatting_group_id = Utils.getInt(response, "chatting_group_id")

                        messageET.setText("")

                        emptyLL.visibility = View.GONE
                        chatLV.visibility = View.VISIBLE

                        timerStart()

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

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

    private fun back() {
        finish()
    }

    fun onClickBack(view: View) {
        back()
    }

    override fun onDestroy() {

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }


        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

        if (timer != null) {
            timer!!.cancel()
        }
    }


    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        lastItemVisibleFlag = totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount

        if (firstVisibleItem == 0 && firstVisibleItem + visibleItemCount < totalItemCount) {
            if (adapterData.size > 0) {
                try {
                    val firstMSG = adapterData[0]
                    val chatting = firstMSG.getJSONObject("Chatting")
                    first_id = Utils.getInt(chatting, "id")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                last_id = -1
            } else {
                first_id = -1
                if (adapterData.size > 0) {
                    try {
                        val lastMSG = adapterData[adapterData.size - 1]
                        val chatting = lastMSG.getJSONObject("Chatting")
                        last_id = Utils.getInt(chatting, "id")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    last_id = -1
                }

            }
        } else {
            first_id = -1
            if (adapterData.size > 0) {
                try {
                    val lastMSG = adapterData[adapterData.size - 1]
                    val chatting = lastMSG.getJSONObject("Chatting")
                    last_id = Utils.getInt(chatting, "id")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                last_id = -1
            }
        }

    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            userScrolled = true
            if (timer != null) {
                timer!!.cancel()
            }
        } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            if (timer != null) {
                timer!!.cancel()
            }
        } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (lastItemVisibleFlag) {
                if (adapterData.size > 0) {
                    val lastMSG = adapterData[adapterData.size - 1]
                    first_id = -1
                    try {
                        val chatting = lastMSG.getJSONObject("Chatting")
                        last_id = Utils.getInt(chatting, "id")
                    } catch (e: NumberFormatException) {
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            } else {
                /*
                if (first_id > 0) {
                    loadData();
                }
                */
            }

            if (adapterData.size > 0) {
                if (timer != null) {
                    timer!!.cancel()
                }

                val task = object : TimerTask() {
                    override fun run() {
                        loadDataHandler.sendEmptyMessage(0)
                    }
                }

                timer = Timer()
                timer!!.schedule(task, 1000, 2000)
            }

        } else {
        }
    }

}
