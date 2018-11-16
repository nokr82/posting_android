package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_match_info.*
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.*

/**
 * Created by dev1 on 2018-02-28.
 */

class MatchInfoActivity : RootActivity() {

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val backPressCloseHandler: BackPressCloseHandler? = null

    var posting_id = ""
    var save_id = -1
    var member_id = -1
    var match_count = 0

    internal var matchCntUpdate: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var type = intent.getStringExtra("type")
                var block_member_id = intent.getIntExtra("block_member_id", -1)

                if(type == "plus") {
                    match_count = match_count + 1;
                } else {
                    match_count = match_count - 1;
                }

                matchCntTV.text = match_count.toString()

                if(block_member_id > 0) {
                    for (i in 0 until addProfileLL.childCount) {
                        var childView = addProfileLL.getChildAt(i)

                        var RL: RelativeLayout = childView.findViewById(R.id.RL)
                        val tag_member_id: Int = RL.getTag() as Int

                        if(tag_member_id == block_member_id) {
                            addProfileLL.removeViewAt(i);
                        }

                    }

                }

            }
        }
    }

    internal var delPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                finish()
            }
        }
    }

    internal var saveDelPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_info)

        this.context = this
        progressDialog = ProgressDialog(context)

        var filter1 = IntentFilter("MATCH_UPDATE")
        registerReceiver(matchCntUpdate, filter1)

        val filter2 = IntentFilter("DEL_POSTING")
        registerReceiver(delPostingReceiver, filter2)

        val filter3 = IntentFilter("SAVE_DEL_POSTING")
        registerReceiver(saveDelPostingReceiver, filter3)

        posting_id = intent.getStringExtra("posting_id")
        save_id = intent.getIntExtra("save_id", -1)

        member_id = PrefUtils.getIntPreference(context, "member_id")

        finishLL.setOnClickListener {
            finish()
        }

        postingRL.setOnClickListener {
            var intent = Intent(context, DetailActivity::class.java);
            intent.putExtra("id", posting_id)
            intent.putExtra("save_id", save_id)
            startActivity(intent)
        }

        loadData()

    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("posting_id", posting_id)

        PostingAction.save_members(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    addProfileLL.removeAllViews()

                    if ("ok" == result) {
                        val postingSaves = response.getJSONArray("postingSaves")

                        val posting = response.getJSONObject("posting")
                        val write_member = response.getJSONObject("member")
                        match_count = Utils.getInt(response, "match_count")
                        val savemember_id = Utils.getInt(write_member, "id")

                        for (i in 0..(postingSaves.length() - 1)) {

                            val data: JSONObject = postingSaves.get(i) as JSONObject
                            val member = data.getJSONObject("Member")

                            val profileView = View.inflate(context, R.layout.item_match_user_profile, null)
                            var profileIV: CircleImageView = profileView.findViewById(R.id.profileIV)
                            var alarmCntTV: TextView = profileView.findViewById(R.id.alarmCntTV)
                            var RL: RelativeLayout = profileView.findViewById(R.id.RL)
                            val savemember_id = Utils.getInt(member, "id")
                            val member_block_yn = Utils.getString(member, "member_block_yn")

                            RL.setTag(savemember_id)

                            if (member_id == savemember_id || member_block_yn == "Y") {
                                RL.visibility = View.GONE
                            } else {
                                var profile_uri = Config.url + Utils.getString(member, "image_uri")
                                ImageLoader.getInstance().displayImage(profile_uri, profileIV, Utils.UILoptionsProfile)
                            }

                            profileIV.setOnClickListener {

                                if (Utils.getInt(member, "id") != member_id) {
                                    var intent = Intent(context, ChattingActivity::class.java)
                                    intent.putExtra("attend_member_id", Utils.getInt(member, "id"))
                                    intent.putExtra("posting_id", posting_id.toInt())
                                    startActivity(intent)

                                    alarmCntTV.visibility = View.GONE

                                }

                            }

                            val new_message_count = Utils.getInt(data, "new_message_count")

                            if (new_message_count < 1) {
                                alarmCntTV.visibility = View.GONE
                            } else {
                                alarmCntTV.visibility = View.VISIBLE
                                alarmCntTV.text = new_message_count.toString()
                            }

                            val params = RL.layoutParams as LinearLayout.LayoutParams
                            params.setMargins(0, 0, 6, 0)
                            RL.layoutParams = params

                            addProfileLL.addView(profileView)
                        }

                        var image_uri = Utils.getString(posting, "image_uri")
                        if (image_uri.isEmpty() || image_uri == "") {
                            contentsTV.visibility = View.VISIBLE
                            contentsTV.text = Utils.getString(posting, "contents")
                        } else {
                            imageIV.visibility = View.VISIBLE
                            var posting_uri = Config.url + Utils.getString(posting, "image_uri")
                            ImageLoader.getInstance().displayImage(posting_uri, imageIV, Utils.UILoptionsPosting)

                        }

                        var postingCnt = ""
                        val count = Utils.getInt(posting, "count")
                        if(count < 1) {
                            postingCnt = "∞"
                        } else {
                            postingCnt = count.toString()
                        }

                        // 나 자신은 빼야되기 때문!
                        var cnt = postingSaves.length() - 1
                        postingCntTV.text = cnt.toString() + "/" + postingCnt

                        matchCntTV.text = match_count.toString()

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
        super.onDestroy()

        progressDialog = null

        try {
            if (matchCntUpdate != null) {
                context!!.unregisterReceiver(matchCntUpdate)
            }

        } catch (e: IllegalArgumentException) {
        }

        try {
            if (delPostingReceiver != null) {
                context!!.unregisterReceiver(delPostingReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }

        try {
            if (saveDelPostingReceiver != null) {
                context!!.unregisterReceiver(saveDelPostingReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }
    }


}
