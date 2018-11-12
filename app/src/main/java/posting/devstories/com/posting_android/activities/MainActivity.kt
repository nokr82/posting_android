package posting.devstories.com.posting_android.activities

import android.app.Activity
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

    val CONFRIM_SCHOOL = 301;

    var tabType = 1
    var type :String?= null
    var tabWriteV: View? = null
    var member_id = -1
    var member_type = ""
    var is_push = false
    var posting_id:String?=null
    var chatting_member_id:String = "-1"

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

    internal var setViewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {

//                if(fragmentFT.currentTab != 0) {
//                    fragmentFT.onTabChanged("post")
//                }

            }
        }
    }

    private var confirm_yn = ""
    private var active_yn = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.context = this

        posting_id = intent.getStringExtra("posting_id")
        is_push = intent.getBooleanExtra("is_push", false)
        type = intent.getStringExtra("intent")

        confirm_yn = PrefUtils.getStringPreference(context, "confirm_yn")
        active_yn = PrefUtils.getStringPreference(context, "active_yn")

        if(is_push) {

            posting_id = intent.getStringExtra("posting_id")
            chatting_member_id = intent.getStringExtra("chatting_member_id")

            if(posting_id != "" && posting_id != null && posting_id != "-1") {
                var intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", posting_id)
                startActivity(intent)
            } else {
                var intent = Intent(context, ChattingActivity::class.java)
                intent.putExtra("attend_member_id", chatting_member_id.toInt())
                startActivity(intent)
            }

        }

        val filter3 = IntentFilter("EDIT_POSTING")
        context.registerReceiver(editPostingReceiver, filter3)

        val filter1 = IntentFilter("UPDATE_ALARM_CNT")
        context.registerReceiver(updateAlarmCntReceiver, filter1)

        val filter2 = IntentFilter("SET_VIEW")
        context.registerReceiver(setViewReceiver, filter2)

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

        if(member_type.equals("3")) {
            setTabBar()
            myPageIV.setImageResource(R.mipmap.clickmy)
            fragmentFT.currentTab = 2
        }

        homeLL.setOnClickListener {

            setTabBar()

            homeIV.setImageResource(R.mipmap.home)

            val school_id = PrefUtils.getIntPreference(context, "school_id")
            PrefUtils.setPreference(context, "current_school_id", school_id)

            val postFragment = supportFragmentManager.findFragmentByTag("post") as? PostFragment

            if(postFragment != null) {
                if(fragmentFT.currentTab == 0) {
                    postFragment.setMainView()
                } else {
                    postFragment.disableOnPageSelected()
                }
            }

            fragmentFT.onTabChanged("post")
        }

        writeLL.setOnClickListener {

            if(member_type.equals("2")) {
                if("N" == confirm_yn) {
                    var intent = Intent(context, DlgCommonActivity::class.java)
                    intent.putExtra("contents", "학교 인증 후 이용하실 수 있습니다")
                    startActivityForResult(intent, CONFRIM_SCHOOL)

                    return@setOnClickListener
                }
            } else {
                if(active_yn == "N") {
                    var intent = Intent(context, DlgCommonActivity::class.java)
                    intent.putExtra("contents", "사업자 인증 후 이용하실 수 있습니다")
                    startActivity(intent)

                    return@setOnClickListener
                }
            }


            val postFragment = supportFragmentManager.findFragmentByTag("post") as? PostFragment

            var tabType = -1
            if(postFragment != null) {
                if(fragmentFT.currentTab == 0) {
                    tabType = postFragment.tabType
                }
            }


            val current_school = PrefUtils.getIntPreference(context, "current_school_id")
            val school_id = PrefUtils.getIntPreference(context, "school_id")

            val intent = Intent(this, PostWriteActivity::class.java)
            intent.putExtra("current_school",current_school)
            intent.putExtra("school_id",school_id)
            intent.putExtra("member_type",member_type)
            intent.putExtra("tabType", tabType)

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

        try {
            if (updateAlarmCntReceiver != null) {
                context.unregisterReceiver(updateAlarmCntReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }
        try {
            if (editPostingReceiver != null) {
                context.unregisterReceiver(editPostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            CONFRIM_SCHOOL -> {
                if(Activity.RESULT_OK == resultCode) {

                    var intent = Intent(context, SchoolagreeActivity::class.java)
                    intent.putExtra("has_branch_yn", "N")
                    intent.putExtra("school_email_confirmed", "N")
                    intent.putExtra("school_confirmed", "N")
                    startActivity(intent)

                }
            }
        }

    }

}
