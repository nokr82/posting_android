package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTabHost
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MyPageFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null

    lateinit var mainActivity:MainActivity

    lateinit var fragmentFT: FragmentTabHost
    lateinit var fragmentFL: FrameLayout

    lateinit var alarmCntTV: TextView

    lateinit var postingV: View
    lateinit var postingRL: RelativeLayout

    lateinit var storageV: View
    lateinit var storageRL: RelativeLayout

    lateinit var notifyV: View
    lateinit var notiRL: RelativeLayout

    lateinit var nickNameTV: TextView
    lateinit var menuIV: ImageView
    lateinit var myIV: CircleImageView

    var tabType = 1

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

    internal var editProfileReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                loadData()
            }
        }
    }

    fun getPostingTabType(): Int {
        val myPagePostingFragment = childFragmentManager.findFragmentByTag("posting") as? MyPagePostingFragment
        if(myPagePostingFragment != null) {
            return myPagePostingFragment.tabType
        }

        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        val filter1 = IntentFilter("UPDATE_ALARM_CNT")
        myContext!!.registerReceiver(updateAlarmCntReceiver, filter1)

        val filter2 = IntentFilter("EDIT_PROFILE")
        myContext!!.registerReceiver(editProfileReceiver, filter2)

        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fra_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentFT = view.findViewById(R.id.fragmentFT)
        fragmentFL = view.findViewById(R.id.fragmentFL)

        nickNameTV = view.findViewById(R.id.nickNameTV)
        menuIV = view.findViewById(R.id.menuIV)
        myIV = view.findViewById(R.id.myIV)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentFT.setup(myContext, childFragmentManager, R.id.fragmentFL)

        val tabPostingV = View.inflate(myContext, R.layout.tab_my_page_posting_view, null)
        val tabStorageV = View.inflate(myContext, R.layout.tab_my_page_storage_view, null)
        val tabNotiV = View.inflate(myContext, R.layout.tab_my_page_noti_view, null)

        postingV = tabPostingV.findViewById(R.id.postingV)
        postingRL = tabPostingV.findViewById(R.id.postingRL)

        storageV = tabStorageV.findViewById(R.id.storageV)
        storageRL = tabStorageV.findViewById(R.id.storageRL)

        notifyV = tabNotiV.findViewById(R.id.notifyV)
        notiRL = tabNotiV.findViewById(R.id.notiRL)
        alarmCntTV = tabNotiV.findViewById(R.id.alarmCntTV)

        fragmentFT.tabWidget.dividerDrawable = null

        fragmentFT.addTab(fragmentFT.newTabSpec("posting").setIndicator(tabPostingV), MyPagePostingFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("storage").setIndicator(tabStorageV), MyPageStorageFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("notify").setIndicator(tabNotiV), MyPageNotifyFragment::class.java, null)


        menuIV.setOnClickListener {
            val intent = Intent(myContext, MyPageActivity::class.java)
            startActivity(intent)
        }


        postingRL.setOnClickListener {

            setTabView()
            postingV.visibility = View.VISIBLE

            loadData()
            fragmentFT.onTabChanged("posting")
        }

        storageRL.setOnClickListener {

            setTabView()
            storageV.visibility = View.VISIBLE

            fragmentFT.onTabChanged("storage")
        }

        notiRL.setOnClickListener {

            setTabView()
            notifyV.visibility = View.VISIBLE

            fragmentFT.onTabChanged("notify")
        }

        loadData()

    }

    fun setTabView(){
        postingV.visibility = View.INVISIBLE
        storageV.visibility = View.INVISIBLE
        notifyV.visibility = View.INVISIBLE
    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))

        MemberAction.my_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var alarm_count = Utils.getInt(response, "alarm_count")

                        if(alarm_count < 1) {
                            alarmCntTV.visibility = View.GONE
                        } else {
                            alarmCntTV.visibility = View.VISIBLE
                            alarmCntTV.text = alarm_count.toString()
                        }


                        var member = response.getJSONObject("member")
                        var image_uri = Utils.getString(member, "image_uri")
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            ImageLoader.getInstance().displayImage(image,myIV, Utils.UILoptionsPosting)
                        }
                        nickNameTV.text = Utils.getString(member, "nick_name")

                    } else {
                        Toast.makeText(myContext, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
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

    override fun onDestroy() {
        super.onDestroy()

        try {
            if (updateAlarmCntReceiver != null) {
                myContext!!.unregisterReceiver(updateAlarmCntReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }
        try {
            if (editProfileReceiver != null) {
                myContext!!.unregisterReceiver(editProfileReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }

}
