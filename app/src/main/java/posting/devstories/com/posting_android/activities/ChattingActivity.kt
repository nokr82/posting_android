package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_chatting.*
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.ChattingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ChattingAdapter
import posting.devstories.com.posting_android.base.BackPressCloseHandler
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import java.util.*

/**
 * Created by dev1 on 2018-02-28.
 */

class ChattingActivity : RootActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        this.context = this
        progressDialog = ProgressDialog(context)

        attend_member_id = intent.getIntExtra("attend_member_id", -1)

        backLL.setOnClickListener {
            finish()
        }

        adapter = ChattingAdapter(this, R.layout.item_chat, adapterData)
        chatLV.adapter = adapter

        messageET.setOnEditorActionListener { v, actionId, event ->

            when(actionId) {
                EditorInfo.IME_ACTION_SEND -> {

                    val message = Utils.getString(messageET)

                    if(message == "" || message.isEmpty()) {
                        return@setOnEditorActionListener true
                    }

                    if(emptyLL.visibility == View.VISIBLE) {
                        chattingAdd("t", message)
                    } else {
                        sendMessage("t", message)
                    }

                }
            }

            return@setOnEditorActionListener true
        }


        chattingCheck()

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
        }

        ChattingAction.sendMessage(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    messageET.setText("")

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

        progressDialog = null

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

}
