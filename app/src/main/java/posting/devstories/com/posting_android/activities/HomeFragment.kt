package posting.devstories.com.posting_android.activities

import `in`.srain.cube.views.GridViewWithHeaderAndFooter
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
import posting.devstories.com.posting_android.adapter.*
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.NonSwipeableViewPager
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class HomeFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null

    var adverImagePathObjs = ArrayList<JSONObject>()
    private lateinit var adverAdapter: NewFullScreenImageAdapter
    var adPosition = 0;

    private var adTime = 0
    private var handler: Handler? = null

    lateinit var mainAdapter: HomePostAdapter
    var mainAdapterData = ArrayList<JSONObject>();

    var keyword = ""
    var type = ""
    var tabType = 1

    var page = 1

    var member_id = -1

    var current_school_id = -1

    private var onPageSelectedEnabled = true

    lateinit var adverVP: ViewPager
    lateinit var pagerAdapter: PagerAdapter

    lateinit var circleLL: LinearLayout
    lateinit var menuLL: LinearLayout

    lateinit var homeGV: GridViewWithHeaderAndFooter
    lateinit var pagerVP: NonSwipeableViewPager

    lateinit var univIV: ImageView

    lateinit var schoolLV: ListView

    lateinit var mainLL: LinearLayout

    lateinit var searchET: EditText

    lateinit var mainActivity: MainActivity

    private var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    private lateinit var adapter: SchoolAdapter

    internal var savePostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var posting_id = intent.getStringExtra("posting_id")
                var count = intent.getIntExtra("count", 1)

                var index = -1

                for (i in 0..(mainAdapterData.size - 1)) {
                    var data = mainAdapterData[i]
                    var list = data.getJSONArray("list")

                    for (j in 0..(list.length() - 1)) {

                        var p: JSONObject = list[j] as JSONObject
                        var posting = p.getJSONObject("Posting")

                        if (Utils.getString(posting, "id") == posting_id) {

                            if (Utils.getInt(posting, "leftCount") != 9999) {
                                var cnt = Utils.getInt(posting, "count") - count

                                if(cnt < 1) {
                                    list.remove(j)

                                } else {
                                    posting.put("leftCount", cnt)
                                }
                            }

                            break
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

            if (intent != null) {
                tabType = intent!!.getIntExtra("tabType", 1)
                val type = tabType - 1

                if (type == pagerVP.currentItem) {
                    setMenuTabView()
                }

                pagerVP.currentItem = type
            }

        }
    }

    internal var writePostReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {
                page = 1
                mainData()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        progressDialog = ProgressDialog(myContext)

        mainActivity = activity as MainActivity
        val filter2 = IntentFilter("DEL_POSTING")
        mainActivity.registerReceiver(delPostingReceiver, filter2)
        val filter1 = IntentFilter("SAVE_POSTING")
        mainActivity.registerReceiver(savePostingReceiver, filter1)
        val filter3 = IntentFilter("SET_VIEW")
        mainActivity.registerReceiver(setViewReceiver, filter3)
        val filter4 = IntentFilter("WRITE_POST")
        mainActivity.registerReceiver(writePostReceiver, filter4)

        return inflater.inflate(R.layout.fra_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeGV = view.findViewById(R.id.homeGV)

        val layoutInflater = LayoutInflater.from(context)
        val header = layoutInflater.inflate(R.layout.item_home_header, null)

        adverVP = header.findViewById(R.id.adverVP)
        circleLL = header.findViewById(R.id.circleLL)

        homeGV.addHeaderView(header)

        menuLL = view.findViewById(R.id.menuLL)

        pagerVP = view.findViewById(R.id.pagerVP)

        univIV = view.findViewById(R.id.univIV)

        mainLL = view.findViewById(R.id.mainLL)

        schoolLV = view.findViewById(R.id.schoolLV)

        searchET = view.findViewById(R.id.searchET)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        val image_uri = PrefUtils.getStringPreference(myContext, "current_school_image_uri")
//        var univimg = Config.url + image_uri
//        ImageLoader.getInstance().displayImage(univimg, univIV, Utils.UILoptionsUserProfile)

        member_id = PrefUtils.getIntPreference(myContext, "member_id")

        // 메인 데이터
        mainAdapter = HomePostAdapter(myContext, R.layout.item_post, mainAdapterData)
        homeGV.adapter = mainAdapter
        homeGV.setOnItemClickListener { parent, view, position, id ->

            val json = mainAdapterData.get(position)
            val posting = json.getJSONObject("Posting")
            var posting_id = Utils.getString(posting, "id")

            var intent = Intent(context, DlgDetailActivity::class.java)
            intent.putExtra("id", posting_id)
            startActivity(intent)

        }

        // 메인 광고 뷰페이저
        adverAdapter = NewFullScreenImageAdapter(mainActivity, adverImagePathObjs)
        adverVP.adapter = adverAdapter
        adverVP.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                adPosition = position
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                circleLL.removeAllViews()
                for (i in adverImagePathObjs.indices) {
                    if (i == adPosition) {
                        addDot(circleLL, true)
                    } else {
                        addDot(circleLL, false)
                    }
                }
            }
        })


        // 뷰페이저
        pagerAdapter = PagerAdapter(getChildFragmentManager(), searchET)
        pagerVP.adapter = pagerAdapter
        pagerVP.offscreenPageLimit = 1
        pagerAdapter.notifyDataSetChanged()
        pagerVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                if (!onPageSelectedEnabled) {
                    return
                }

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
            val intent = Intent(myContext, MyPageActivity::class.java)
            startActivity(intent)
        }

        univIV.setOnClickListener {
            setMainView()
            mainData()
        }

        searchET.setOnEditorActionListener { textView, i, keyEvent ->

            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {

                    keyword = Utils.getString(searchET)

                    if (mainLL.visibility == View.GONE) {
                        var intent = Intent()
                        intent.putExtra("keyword", keyword)
                        intent.action = "SEARCH_KEYWORD"
                        myContext!!.sendBroadcast(intent)
                    }

                    Utils.hideKeyboard(myContext)

                }
            }
            return@setOnEditorActionListener true
        }

        timer()

        setMainView()
        // mainData()

        // 학교 검색
        searchET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                // you can call or do what you want with your EditText here

                // yourEditText...

                if(mainLL.visibility == View.VISIBLE) {
                    val keyword = Utils.getString(searchET)

                    searchSchool(keyword)
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        adapter = SchoolAdapter(myContext!!, R.layout.school_item, adapterData)
        schoolLV.adapter = adapter
        adapter.notifyDataSetChanged()

        schoolLV.setOnItemClickListener { adapterView, view, i, l ->
            if (adapterData.size > i) {
                val schoolO = adapterData.get(i)
                val school = schoolO.getJSONObject("School")
                val school_id = Utils.getInt(school, "id")

                PrefUtils.setPreference(myContext, "current_school_id", school_id)

                val intent = Intent(myContext, MainActivity::class.java)
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

                    if ("ok" == result && dbSearchKeyword == searchKeyword) {

                        for (i in 0..(list.length() - 1)) {
                            var data = list.get(i) as JSONObject
                            checkSchoolData(data)
                        }

                        adapter.notifyDataSetChanged()

                    } else {

                    }

                    if (adapterData.size == 0) {
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

    fun checkSchoolData(data: JSONObject) {

        var add = true

        val addData = data.getJSONObject("School")

        for (i in 0..(adapterData.size - 1)) {
            val json = adapterData.get(i)
            val school = json.getJSONObject("School")

            if (Utils.getString(school, "id") == Utils.getString(addData, "id")) {
                add = false
            }

        }

        if (add) {
            adapterData.add(data)
        }
    }

    private fun timer() {

        if(handler != null) {
            handler!!.removeCallbacksAndMessages(null);
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {

                adTime++

                val index = adverVP.getCurrentItem()
                val last_index = adverImagePathObjs.size - 1

                if (adTime % 2 == 0) {
                    if (index < last_index) {
                        adverVP.setCurrentItem(index + 1)
                    } else {
                        adverVP.setCurrentItem(0)
                    }
                }

                handler!!.sendEmptyMessageDelayed(0, 2000) // 1초에 한번 업, 1000 = 1 초
            }
        }
        handler!!.sendEmptyMessage(0)
    }

    private fun addDot(circleLL: LinearLayout, selected: Boolean) {
        val iv = ImageView(myContext)
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

    class PagerAdapter(fm: FragmentManager, searchET: EditText) : FragmentStatePagerAdapter(fm) {

        var searchET = searchET

        override fun getItem(i: Int): Fragment {

            var fragment: Fragment

            val args = Bundle()
            args.putString("keyword", Utils.getString(searchET))

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

    fun setMainView() {

        mainLL.visibility = View.VISIBLE
        pagerVP.visibility = View.GONE

        searchET.hint = "학교검색"

        mainData()
    }

    fun setMenuTabView() {

        onPageSelectedEnabled = true

        mainLL.visibility = View.GONE
        pagerVP.visibility = View.VISIBLE

        searchET.hint = "검색"

    }

    fun mainData() {
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("current_school_id", current_school_id)
        params.put("type", type)
        params.put("page", page)

        PostingAction.new_main(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        if (page == 1) {
                            mainAdapterData.clear()
                            adapter.notifyDataSetChanged()
                        }

                        var alarm_count = Utils.getInt(response, "alarm_count")

                        if (alarm_count < 1) {
                            mainActivity.alarmCntTV.visibility = View.GONE
                        } else {
                            mainActivity.alarmCntTV.visibility = View.VISIBLE
                            mainActivity.alarmCntTV.text = alarm_count.toString()
                        }

                        var adver = response.getJSONArray("adver")
                        adverImagePathObjs.clear()

                        for(i in 0 until adver.length()) {
                            val adverObj:JSONObject = adver[i] as JSONObject
                            val advertise = adverObj.getJSONObject("Advertise")

                            var image_uri = Config.url + Utils.getString(advertise, "image_uri")

                            adverImagePathObjs.add(adverObj);

                        }

                        val list = response.getJSONArray("list")
                        val current_school_id = Utils.getInt(response, "current_school_id")

                        if (current_school_id > 0) {
                            var school = response.getJSONObject("school")
                            val schoolindex = school.getJSONObject("School")
                            val image_uri = Utils.getString(schoolindex, "image_uri")

                            PrefUtils.setPreference(myContext, "current_school_image_uri ", image_uri)

                            val current_school_image_uri = PrefUtils.getStringPreference(myContext, "current_school_image_uri")

                            var univimg = Config.url + current_school_image_uri
                            ImageLoader.getInstance().displayImage(univimg, univIV, Utils.UILoptionsUserProfile)
                        }

                        for (i in 0..(list.length() - 1)) {
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

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        try {
            if (savePostingReceiver != null) {
                myContext!!.unregisterReceiver(savePostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

        try {
            if (setViewReceiver != null) {
                myContext!!.unregisterReceiver(setViewReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

        try {
            if (delPostingReceiver != null) {
                myContext!!.unregisterReceiver(delPostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }

    fun disableOnPageSelected() {
        onPageSelectedEnabled = false
    }

}
