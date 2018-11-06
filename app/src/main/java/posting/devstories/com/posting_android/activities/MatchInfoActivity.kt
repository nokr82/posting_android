package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import posting.devstories.com.posting_android.base.BackPressCloseHandler
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import java.util.zip.Inflater

/**
 * Created by dev1 on 2018-02-28.
 */

class MatchInfoActivity : RootActivity() {

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val backPressCloseHandler: BackPressCloseHandler? = null

    var posting_id = ""
    var member_id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_info)

        this.context = this
        progressDialog = ProgressDialog(context)

        posting_id = intent.getStringExtra("posting_id")

        member_id = PrefUtils.getIntPreference(context, "member_id")

        finishLL.setOnClickListener {
            finish()
        }

        postingRL.setOnClickListener {
            var intent = Intent(context, DetailActivity::class.java);
            intent.putExtra("id", posting_id)
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
                        val member = response.getJSONObject("member")
                        val match_count = Utils.getString(response, "match_count")

                        for (i in 0..(postingSaves.length() - 1)) {

                            val data:JSONObject = postingSaves.get(i) as JSONObject
                            val member = data.getJSONObject("Member")

                            val profileView = View.inflate(context, R.layout.item_match_user_profile, null)
                            var profileIV:CircleImageView = profileView.findViewById(R.id.profileIV)
                            var alarmCntTV:TextView = profileView.findViewById(R.id.alarmCntTV)

                            var profile_uri = Config.url + Utils.getString(member,"image_uri")
                            ImageLoader.getInstance().displayImage(profile_uri, profileIV, Utils.UILoptionsProfile)

                            profileIV.setOnClickListener {

                                if(Utils.getInt(member, "id") != member_id) {
                                    var intent = Intent(context, ChattingActivity::class.java)
                                    intent.putExtra("attend_member_id", Utils.getInt(member, "id"))
                                    startActivity(intent)
                                }

                            }

                            val new_message_count = Utils.getInt(response, "new_message_count")

                            if(new_message_count < 1) {
                                alarmCntTV.visibility = View.GONE
                            } else {
                                alarmCntTV.visibility = View.VISIBLE
                                alarmCntTV.text = new_message_count.toString()
                            }

                            addProfileLL.addView(profileView)
                        }


                        var posting_uri = Config.url + Utils.getString(posting,"image_uri")
                        ImageLoader.getInstance().displayImage(posting_uri, imageIV, Utils.UILoptionsPosting)

                        postingCntTV.text = postingSaves.length().toString() + "/" + Utils.getString(posting, "count")

                        matchCntTV.text = match_count

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


}
