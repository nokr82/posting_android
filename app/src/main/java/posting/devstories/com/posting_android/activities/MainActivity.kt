package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_home_view.*
import kotlinx.android.synthetic.main.tab_mypage_view.*
import kotlinx.android.synthetic.main.tab_write_view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

class MainActivity : FragmentActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var context: Context
    private val BACK_PRESSED_TERM = (1000 * 2).toLong()
    private var backPressedTime: Long = 0

    var tabType = 1
    var type = ""
    var tabWriteV: View? = null
    var member_id = -1
    var member_type = ""
    var is_push = false
    var posting_id:String?=null


    internal var editPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var posting_id:Int = intent.getIntExtra("posting_id", -1)

            }
        }
    }

    internal var updateAlarmCntReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var alarm_count:Int = intent.getIntExtra("alarm_count", 0)

                if(alarm_count < 1) {
                    alarmCntTV.visibility = View.GONE
                } else {
                    alarmCntTV.visibility = View.VISIBLE
                    alarmCntTV.text = alarm_count.toString()
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.context = this

        posting_id = intent.getStringExtra("posting_id")
        is_push = intent.getBooleanExtra("is_push", false)

        if(is_push) {

            posting_id = intent.getStringExtra("posting_id")

            var intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("id", posting_id)
            startActivity(intent)
        }

        val filter3 = IntentFilter("EDIT_POSTING")
        context.registerReceiver(editPostingReceiver, filter3)

        val filter1 = IntentFilter("UPDATE_ALARM_CNT")
        context.registerReceiver(updateAlarmCntReceiver, filter1)

        fragmentFT.setup(context, supportFragmentManager, R.id.fragmentFL)

        val tabHomeV = View.inflate(context, R.layout.tab_home_view, null)
        val tabMypageV = View.inflate(context, R.layout.tab_mypage_view, null)
        tabWriteV = View.inflate(context, R.layout.tab_write_view, null)

        fragmentFT.tabWidget.dividerDrawable = null

        member_type = PrefUtils.getStringPreference(context, "member_type")

        fragmentFT.addTab(fragmentFT.newTabSpec("post").setIndicator(tabHomeV), PostFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("write").setIndicator(tabWriteV), WriteFragment::class.java, null)

        if(member_type.equals("3")){
            fragmentFT.addTab(fragmentFT.newTabSpec("myPage").setIndicator(tabMypageV), OrderPageFragment::class.java, null)
        }else{
            fragmentFT.addTab(fragmentFT.newTabSpec("myPage").setIndicator(tabMypageV), MyPageFragment::class.java, null)
        }

        homeLL.setOnClickListener {

            setTabBar()

            homeIV.setImageResource(R.mipmap.home)

            fragmentFT.onTabChanged("post")


        }

        writeLL.setOnClickListener {

            val intent = Intent(this, PostWriteActivity::class.java)
            startActivity(intent)


//            setTabBar()
//
//            writeIV.setImageResource(R.mipmap.clickplus)
//            fragmentFT.onTabChanged("write")

        }

        myPageLL.setOnClickListener {

            setTabBar()

            myPageIV.setImageResource(R.mipmap.clickmy)
            fragmentFT.onTabChanged("myPage")

        }

        updateToken()

    }

    fun setTabBar(){
        homeIV.setImageResource(R.mipmap.noclickhome)
        writeIV.setImageResource(R.mipmap.plus)
        myPageIV.setImageResource(R.mipmap.my)
    }

    private fun updateToken() {
        val params = RequestParams()
        val member_id = PrefUtils.getIntPreference(context, "member_id", -1)
        val member_token = FirebaseInstanceId.getInstance().token

        if (member_id == -1 || null == member_token || "" == member_token || member_token.length < 1) {
            return
        }
        params.put("member_id", member_id)
        params.put("token", member_token)
        params.put("device", Config.device)

        MemberAction.regist_token(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {}

            private fun error() {

                if (progressDialog != null) {
                    Utils.alert(context, "조회중 장애가 발생하였습니다.")
                }
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

//                val member_id = PrefUtils.getIntPreference(context, "member_id")
//                LogAction.log(javaClass.toString(), member_id, responseString)

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONArray?
            ) {
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

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < BACK_PRESSED_TERM) {
            finish()
        } else {
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (updateAlarmCntReceiver != null) {
            context.unregisterReceiver(updateAlarmCntReceiver)
        }
    }

}
