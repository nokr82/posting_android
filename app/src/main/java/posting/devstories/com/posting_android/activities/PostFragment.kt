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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.tab_mypage_view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.Actions.SchoolAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.FullScreenImageAdapter
import posting.devstories.com.posting_android.adapter.MainPostAdapter
import posting.devstories.com.posting_android.adapter.SchoolAdapter
import posting.devstories.com.posting_android.base.Config
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

    var keyword = ""
    var type = ""
    var tabType = 1

    var member_id = -1

    lateinit var adverVP: ViewPager
    lateinit var pagerAdapter: PagerAdapter

    lateinit var circleLL: LinearLayout
    lateinit var menuLL: LinearLayout

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

    lateinit var schoolLV:ListView

    lateinit var mainLL:LinearLayout

    lateinit var searchET:EditText

    lateinit var mainActivity:MainActivity

    private var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    private lateinit var adapter: SchoolAdapter

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

                            if(Utils.getInt(posting, "leftCount") != 9999) {
                                var cnt = Utils.getInt(posting, "leftCount") - 1
                                posting.put("leftCount", cnt)
                            }

                        }

                    }
                }

                mainAdapter.notifyDataSetChanged()

            }
        }
    }
    internal var delPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            mainData()
            mainAdapter.notifyDataSetChanged()
        }
    }
    internal var setViewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if(intent != null) {
                tabType = intent!!.getIntExtra("tabType", 1)
                val type =intent!!.getIntExtra("type",-1)

                print("type---------"+type)
                val w_type = type -1

                print("type---------2"+w_type)
                if (w_type == pagerVP.currentItem) {
                    setMenuTabView()
                }
                pagerVP.currentItem = w_type

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
        val filter2 = IntentFilter("DEL_POSTING")
        mainActivity.registerReceiver(delPostingReceiver, filter2)
        val filter1 = IntentFilter("SAVE_POSTING")
        mainActivity.registerReceiver(savePostingReceiver, filter1)
        val filter3 = IntentFilter("SET_VIEW")
        mainActivity.registerReceiver(setViewReceiver, filter3)

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

        menuLL = view.findViewById(R.id.menuLL)

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

        schoolLV = view.findViewById(R.id.schoolLV)

        searchET = view.findViewById(R.id.searchET)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val image_uri =   PrefUtils.getStringPreference(context, "current_school_image_uri")
        var univimg = Config.url +image_uri
        ImageLoader.getInstance().displayImage(univimg, univIV, Utils.UILoptionsUserProfile)
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


        menuLL.setOnClickListener {
            val intent = Intent(context, MyPageActivity::class.java)
            startActivity(intent)
        }


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

        searchET.setOnEditorActionListener { textView, i, keyEvent ->

            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {

                    keyword = Utils.getString(searchET)

                    if(mainLL.visibility == View.VISIBLE) {
                        // 메인 학교 검색

                    } else {
                        var intent = Intent()
                        intent.putExtra("keyword", keyword)
                        intent.putExtra("type", (pagerVP.currentItem + 1))
                        intent.action = "SEARCH_KEYWORD"
                        context!!.sendBroadcast(intent)
                    }

                }
            }
            return@setOnEditorActionListener true
        }

        timer()
        mainData()

        // 학교 검색
        searchET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                // you can call or do what you want with your EditText here

                // yourEditText...

                val keyword = Utils.getString(searchET)

                searchSchool(keyword)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        adapter = SchoolAdapter(context!!, R.layout.school_item, adapterData)
        schoolLV.adapter = adapter
        adapter.notifyDataSetChanged()

        schoolLV.setOnItemClickListener { adapterView, view, i, l ->
            if(adapterData.size > i) {
                val schoolO = adapterData.get(i)
                val school = schoolO.getJSONObject("School")
                val school_id = Utils.getInt(school, "id")

                println("school : $school")
                println("school_id : $school_id")

                PrefUtils.setPreference(context, "current_school_id", school_id)


                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

    }

    fun searchSchool(searchKeyword: String) {

        val params = RequestParams()
        params.put("searchKeyword", searchKeyword)

        SchoolAction.School(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    adapterData.clear()
                    adapter.notifyDataSetChanged()

                    val result = response!!.getString("result")
                    val dbSearchKeyword = response!!.getString("searchKeyword")
                    val list = response!!.getJSONArray("list")

                    println(response)

                    if("ok" == result && dbSearchKeyword == searchKeyword) {

                        for (i in 0..(list.length() - 1)) {
                            var data  = list.get(i) as JSONObject
                            checkSchoolData(data)
                        }

                        adapter.notifyDataSetChanged()

                    } else {

                    }

                    if(adapterData.size == 0) {
                        schoolLV.visibility = View.GONE
                    } else {
                        schoolLV.visibility = View.VISIBLE
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

                    // progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun checkSchoolData(data:JSONObject){

        var add = true

        val addData = data.getJSONObject("School")

        for (i in 0.. (adapterData.size - 1)) {
            val json = adapterData.get(i)
            val school = json.getJSONObject("School")

            if(Utils.getString(school, "id") == Utils.getString(addData, "id")) {
                add = false
            }

        }

        if(add) {
            adapterData.add(data)
        }
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

        searchET.setText("")

    }

    fun mainData() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("current_school_id", PrefUtils.getIntPreference(context, "current_school_id"))
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

                        var alarm_count = Utils.getInt(response, "alarm_count")

                        if(alarm_count < 1) {
                            mainActivity.alarmCntTV.visibility = View.GONE
                        } else {
                            mainActivity.alarmCntTV.visibility = View.VISIBLE
                            mainActivity.alarmCntTV.text = alarm_count.toString()
                        }

                        val list = response.getJSONArray("list")
                        var school = response.getJSONObject("school")
                        val schoolindex = school.getJSONObject("School")
                        val image_uri = Utils.getString(schoolindex,"image_uri")

                        PrefUtils.setPreference(context, "current_school_image_uri ", image_uri )

                      val current_school_image_uri=  PrefUtils.getStringPreference(context, "current_school_image_uri ")

                        var univimg = Config.url +current_school_image_uri
                        ImageLoader.getInstance().displayImage(univimg, univIV, Utils.UILoptionsUserProfile)

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
            } else if (delPostingReceiver != null) {
                context!!.unregisterReceiver(delPostingReceiver)
            }  //브로드캐스트받기
            else if (setViewReceiver != null) {
                context!!.unregisterReceiver(setViewReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }

}
