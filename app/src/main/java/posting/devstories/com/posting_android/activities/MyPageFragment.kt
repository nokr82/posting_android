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
import android.widget.FrameLayout
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fra_my_page.*
import kotlinx.android.synthetic.main.tab_my_page_noti_view.*
import kotlinx.android.synthetic.main.tab_my_page_posting_view.*
import kotlinx.android.synthetic.main.tab_my_page_storage_view.*
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MyPageFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    lateinit var mainActivity:MainActivity

    lateinit var fragmentFT: FragmentTabHost
    lateinit var fragmentFL: FrameLayout

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val filter1 = IntentFilter("UPDATE_ALARM_CNT")
        context!!.registerReceiver(updateAlarmCntReceiver, filter1)

        val filter2 = IntentFilter("EDIT_PROFILE")
        context!!.registerReceiver(editProfileReceiver, filter2)

        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fra_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentFT = view.findViewById(R.id.fragmentFT)
        fragmentFL = view.findViewById(R.id.fragmentFL)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentFT.setup(context, childFragmentManager, R.id.fragmentFL)

        val tabPostingV = View.inflate(context, R.layout.tab_my_page_posting_view, null)
        val tabStorageV = View.inflate(context, R.layout.tab_my_page_storage_view, null)
        val tabNotiV = View.inflate(context, R.layout.tab_my_page_noti_view, null)

        fragmentFT.tabWidget.dividerDrawable = null

        fragmentFT.addTab(fragmentFT.newTabSpec("posting").setIndicator(tabPostingV), MyPagePostingFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("storage").setIndicator(tabStorageV), MyPageStorageFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("notify").setIndicator(tabNotiV), MyPageNotifyFragment::class.java, null)


        menuIV.setOnClickListener {
            val intent = Intent(context, MyPageActivity::class.java)
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
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

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
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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

    override fun onDestroy() {
        super.onDestroy()

        try {
            if (updateAlarmCntReceiver != null) {
                context!!.unregisterReceiver(updateAlarmCntReceiver)
            } else if (editProfileReceiver != null) {
                context!!.unregisterReceiver(editProfileReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }

    }

}
