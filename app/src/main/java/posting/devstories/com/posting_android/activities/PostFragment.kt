package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.FullScreenImageAdapter
import posting.devstories.com.posting_android.adapter.MainPostAdapter
import posting.devstories.com.posting_android.base.NonSwipeableViewPager
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class PostFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null

    var adverImagePaths = ArrayList<String>()
    private var adverAdapterData = ArrayList<JSONObject>()
    private lateinit var adverAdapter: FullScreenImageAdapter
    var adPosition = 0;

    private var adTime = 0
    private lateinit var handler: Handler

    lateinit var mainAdapter: MainPostAdapter
    var mainAdapterData = ArrayList<JSONObject>();

    var type = ""
    var tabType = 1

    var member_id = -1

    lateinit var adverVP: ViewPager
    lateinit var pagerAdapter: PagerAdapter

    lateinit var circleLL: LinearLayout

    lateinit var mainLV: ExpandableHeightListView
    lateinit var pagerVP: NonSwipeableViewPager
    
    lateinit var freeTV:TextView
    lateinit var infoTV:TextView
    lateinit var studyTV:TextView
    lateinit var classTV:TextView
    lateinit var meetingTV:TextView
    lateinit var couponTV:TextView
    
    lateinit var freeV:View
    lateinit var infoV:View
    lateinit var studyV:View
    lateinit var classV:View
    lateinit var meetingV:View
    lateinit var couponV:View

    lateinit var univIV:ImageView

    lateinit var freeRL:RelativeLayout
    lateinit var infoRL:RelativeLayout
    lateinit var studyRL:RelativeLayout
    lateinit var classRL:RelativeLayout
    lateinit var meetingRL:RelativeLayout
    lateinit var couponRL:RelativeLayout

    lateinit var mainLL:LinearLayout

    lateinit var mainActivity:MainActivity

    internal var savePostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var posting_id = intent.getStringExtra("posting_id")

                for (i in 0 .. (mainAdapterData.size - 1)) {
                    var data = mainAdapterData[i]
                    var list = data.getJSONArray("list")

                    for (j in 0 .. (list.length() - 1)) {

                        var p: JSONObject = list[j] as JSONObject
                        var posting = p.getJSONObject("Posting")

                        if(Utils.getString(posting, "id") == posting_id) {
                            var cnt = Utils.getInt(posting, "leftCount") - 1
                            posting.put("leftCount", cnt)
                        }

                    }
                }

                mainAdapter.notifyDataSetChanged()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context
        if (null != ctx) {
            doSomethingWithContext(ctx)
        }

        mainActivity = activity as MainActivity

        val filter1 = IntentFilter("SAVE_POSTING")
        mainActivity.registerReceiver(savePostingReceiver, filter1)

        return inflater.inflate(R.layout.fra_post, container, false)
    }
    fun doSomethingWithContext(context: Context) {
        // TODO: Actually do something with the context
        this.ctx = context
        progressDialog = ProgressDialog(ctx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainLV = view.findViewById(R.id.mainLV)
        adverVP = view.findViewById(R.id.adverVP)
        circleLL = view.findViewById(R.id.circleLL)

        pagerVP = view.findViewById(R.id.pagerVP)

        freeTV = view.findViewById(R.id.freeTV)
        infoTV = view.findViewById(R.id.infoTV)
        studyTV = view.findViewById(R.id.studyTV)
        classTV = view.findViewById(R.id.classTV)
        meetingTV = view.findViewById(R.id.meetingTV)
        couponTV = view.findViewById(R.id.couponTV)

        freeV = view.findViewById(R.id.freeV)
        infoV = view.findViewById(R.id.infoV)
        studyV = view.findViewById(R.id.studyV)
        classV = view.findViewById(R.id.classV)
        meetingV = view.findViewById(R.id.meetingV)
        couponV = view.findViewById(R.id.couponV)

        univIV = view.findViewById(R.id.univIV)

        freeRL = view.findViewById(R.id.freeRL)
        infoRL = view.findViewById(R.id.infoRL)
        studyRL = view.findViewById(R.id.studyRL)
        classRL = view.findViewById(R.id.classRL)
        meetingRL = view.findViewById(R.id.meetingRL)
        couponRL = view.findViewById(R.id.couponRL)

        mainLL = view.findViewById(R.id.mainLL)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        member_id = PrefUtils.getIntPreference(context, "member_id")

        // 메인 데이터
        mainAdapter = MainPostAdapter(ctx, R.layout.item_main, mainAdapterData)
        mainLV.isExpanded = true
        mainLV.adapter = mainAdapter

        // 메인 광고 뷰페이저
        adverAdapter = FullScreenImageAdapter(mainActivity, adverImagePaths)
        adverVP.adapter = adverAdapter
        adverVP.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                adPosition = position
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {
                circleLL.removeAllViews()
                for (i in adverImagePaths.indices) {
                    if (i == adPosition) {
                        addDot(circleLL, true)
                    } else {
                        addDot(circleLL, false)
                    }
                }
            }
        })

        // 뷰페이저
        pagerAdapter = PagerAdapter(getChildFragmentManager())
        pagerVP.adapter = pagerAdapter
        pagerAdapter.notifyDataSetChanged()
        pagerVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }


            override fun onPageSelected(position: Int) {

                when (position) {
                    0 -> {
                        tabType = 1;

                        setMenuTabView()
                    }
                    1 -> {
                        tabType = 2;

                        setMenuTabView()
                    }
                    2 -> {
                        tabType = 3;

                        setMenuTabView()
                    }
                    3 -> {
                        tabType = 4;

                        setMenuTabView()
                    }
                    4 -> {
                        tabType = 5;

                        setMenuTabView()
                    }
                    5 -> {
                        tabType = 6;

                        setMenuTabView()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        univIV.setOnClickListener {
            setMainView()
        }

        freeRL.setOnClickListener {
            if (0 == pagerVP.currentItem) {
                tabType = 1;

                setMenuTabView()
            }
            pagerVP.currentItem = 0
        }

        infoRL.setOnClickListener {
            if (1 == pagerVP.currentItem) {
                tabType = 2;

                setMenuTabView()
            }
            pagerVP.currentItem = 1
        }

        studyRL.setOnClickListener {
            if (2 == pagerVP.currentItem) {
                tabType = 3;

                setMenuTabView()
            }
            pagerVP.currentItem = 2
        }

        classRL.setOnClickListener {
            if (3 == pagerVP.currentItem) {
                tabType = 4;

                setMenuTabView()
            }
            pagerVP.currentItem = 3
        }

        meetingRL.setOnClickListener {
            if (4 == pagerVP.currentItem) {
                tabType = 5;

                setMenuTabView()
            }
            pagerVP.currentItem = 4
        }

        couponRL.setOnClickListener {
            if (5 == pagerVP.currentItem) {
                tabType = 6;

                setMenuTabView()
            }
            pagerVP.currentItem = 5
        }

        timer()
        mainData()

    }

    private fun timer() {
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {

                adTime++

                val index = adverVP.getCurrentItem()
                val last_index = adverAdapterData.size - 1

                if (adTime % 2 == 0) {
                    if (index < last_index) {
                        adverVP.setCurrentItem(index + 1)
                    } else {
                        adverVP.setCurrentItem(0)
                    }
                }

                handler.sendEmptyMessageDelayed(0, 2000) // 1초에 한번 업, 1000 = 1 초
            }
        }
        handler.sendEmptyMessage(0)
    }

    private fun addDot(circleLL: LinearLayout, selected: Boolean) {
        val iv = ImageView(ctx)
        if (selected) {
            iv.setBackgroundResource(R.drawable.circle_background1)
        } else {
            iv.setBackgroundResource(R.drawable.circle_background2)
        }

        val width = Utils.pxToDp(6.0f).toInt()
        val height = Utils.pxToDp(6.0f).toInt()

        iv.layoutParams = LinearLayout.LayoutParams(width, height)
        iv.scaleType = ImageView.ScaleType.CENTER_CROP

        val lpt = iv.layoutParams as ViewGroup.MarginLayoutParams
        val marginRight = Utils.pxToDp(7.0f).toInt()
        lpt.setMargins(0, 0, marginRight, 0)
        iv.layoutParams = lpt

        circleLL.addView(iv)
    }

    class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {

            var fragment: Fragment

            val args = Bundle()
            when (i) {
                0 -> {
                    fragment = FreeFragment()
                    fragment.arguments = args

                    return fragment
                }
                1 -> {
                    fragment = InfoFragment()
                    fragment.arguments = args

                    return fragment
                }
                2 -> {
                    fragment = StudyFragment()
                    fragment.arguments = args
                    return fragment
                }
                3 -> {
                    fragment = ClassFragment()
                    fragment.arguments = args
                    return fragment
                }
                4 -> {
                    fragment = MeetingFragment()
                    fragment.arguments = args
                    return fragment
                }
                5 -> {
                    fragment = CouponFragment()
                    fragment.arguments = args
                    return fragment
                }
                else -> {
                    fragment = FreeFragment()
                    fragment.arguments = args
                    return fragment
                }
            }
        }

        override fun getCount(): Int {
            return 6
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return ""
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }

    fun setMainView(){
        freeTV.setTextColor(Color.parseColor("#A19F9B"))
        infoTV.setTextColor(Color.parseColor("#A19F9B"))
        studyTV.setTextColor(Color.parseColor("#A19F9B"))
        classTV.setTextColor(Color.parseColor("#A19F9B"))
        meetingTV.setTextColor(Color.parseColor("#A19F9B"))
        couponTV.setTextColor(Color.parseColor("#A19F9B"))

        freeV.visibility = View.INVISIBLE
        infoV.visibility = View.INVISIBLE
        studyV.visibility = View.INVISIBLE
        classV.visibility = View.INVISIBLE
        meetingV.visibility = View.INVISIBLE
        couponV.visibility = View.INVISIBLE

        mainLL.visibility = View.VISIBLE
        pagerVP.visibility = View.GONE

        mainData()
    }

    fun setMenuTabView() {
        freeTV.setTextColor(Color.parseColor("#A19F9B"))
        infoTV.setTextColor(Color.parseColor("#A19F9B"))
        studyTV.setTextColor(Color.parseColor("#A19F9B"))
        classTV.setTextColor(Color.parseColor("#A19F9B"))
        meetingTV.setTextColor(Color.parseColor("#A19F9B"))
        couponTV.setTextColor(Color.parseColor("#A19F9B"))

        freeV.visibility = View.INVISIBLE
        infoV.visibility = View.INVISIBLE
        studyV.visibility = View.INVISIBLE
        classV.visibility = View.INVISIBLE
        meetingV.visibility = View.INVISIBLE
        couponV.visibility = View.INVISIBLE

        mainLL.visibility = View.GONE
        pagerVP.visibility = View.VISIBLE

        if(tabType == 1) {
            freeTV.setTextColor(Color.parseColor("#01b4ec"))
            freeV.visibility = View.VISIBLE
        } else if (tabType == 2) {
            infoV.visibility = View.VISIBLE
            infoTV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 3) {
            studyV.visibility = View.VISIBLE
            studyTV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 4) {
            classV.visibility = View.VISIBLE
            classTV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 5) {
            meetingV.visibility = View.VISIBLE
            meetingTV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 6) {
            couponV.visibility = View.VISIBLE
            couponTV.setTextColor(Color.parseColor("#01b4ec"))
        }

    }

    fun mainData() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("type",type)

        PostingAction.mainlist(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    adverImagePaths.clear()
                    adverAdapterData.clear()
                    mainAdapterData.clear()

                    var path = "http://13.124.13.37/data/ad/5ba1ebab-0018-486f-ace1-624cac1f0bcc";

                    adverImagePaths.add(path);
                    adverImagePaths.add(path);
                    adverImagePaths.add(path);
                    adverImagePaths.add(path);
                    adverImagePaths.add(path);
                    adverImagePaths.add(path);

                    var data = JSONObject();
                    data.put("path", path)

                    adverAdapterData.add(data)
                    adverAdapterData.add(data)
                    adverAdapterData.add(data)
                    adverAdapterData.add(data)
                    adverAdapterData.add(data)
                    adverAdapterData.add(data)

                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val list = response.getJSONArray("list")

                        for (i in 0..(list.length()-1)){
                            mainAdapterData.add(list[i] as JSONObject)
                        }

                        mainAdapter.notifyDataSetChanged()
                        adverAdapter.notifyDataSetChanged()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
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

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        try {
            if (savePostingReceiver != null) {
                context!!.unregisterReceiver(savePostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }

}
