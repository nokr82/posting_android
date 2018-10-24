package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
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
import posting.devstories.com.posting_android.adapter.MainAdapter
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.base.NonSwipeableViewPager
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class PostFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterMain: PostAdapter

    var adverImagePaths = java.util.ArrayList<String>()
    private var adverAdapterData = java.util.ArrayList<JSONObject>()
    private lateinit var adverAdapter: FullScreenImageAdapter
    var adPosition = 0;

    private var adTime = 0
    private lateinit var handler: Handler

    lateinit var mainAdapter: MainAdapter
    var mainAdapterData = java.util.ArrayList<JSONObject>();

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context
        if (null != ctx) {
            doSomethingWithContext(ctx)
        }

        mainActivity = activity as MainActivity
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
        mainAdapter = MainAdapter(ctx, R.layout.item_main, mainAdapterData)
        mainLV.isExpanded = true
        mainLV.adapter = mainAdapter


        maindata()

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

                        freeTV.setTextColor(Color.parseColor("#01b4ec"))
                        freeV.visibility = View.VISIBLE

                    }
                    1 -> {
                        tabType = 2;

                        setMenuTabView()

                        infoV.visibility = View.VISIBLE
                        infoTV.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    2 -> {
                        tabType = 3;

                        setMenuTabView()

                        studyV.visibility = View.VISIBLE
                        studyTV.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    3 -> {
                        tabType = 4;

                        setMenuTabView()

                        classV.visibility = View.VISIBLE
                        classTV.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    4 -> {
                        tabType = 5;

                        setMenuTabView()

                        meetingV.visibility = View.VISIBLE
                        meetingTV.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    5 -> {
                        tabType = 6;

                        setMenuTabView()

                        couponV.visibility = View.VISIBLE
                        couponTV.setTextColor(Color.parseColor("#01b4ec"))
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
            pagerVP.currentItem = 0

        }

        infoRL.setOnClickListener {
            pagerVP.currentItem = 1
        }

        studyRL.setOnClickListener {
            pagerVP.currentItem = 2
        }

        classRL.setOnClickListener {
            pagerVP.currentItem = 3
        }

        meetingRL.setOnClickListener {
            pagerVP.currentItem = 4
        }

        couponRL.setOnClickListener {
            pagerVP.currentItem = 5
        }

        timer()
        mainLoadData()

    }

    private fun mainLoadData(){

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

        adverAdapter.notifyDataSetChanged()

        data = JSONObject();
        data.put("type", "free")
        mainAdapterData.add(data);

        data = JSONObject();
        data.put("type", "info")
        mainAdapterData.add(data);

        data = JSONObject();
        data.put("type", "study")
        mainAdapterData.add(data);

        data = JSONObject();
        data.put("type", "class")
        mainAdapterData.add(data);

        data = JSONObject();
        data.put("type", "meeting")
        mainAdapterData.add(data);

        data = JSONObject();

        data.put("type", "coupon")
        mainAdapterData.add(data);

        mainAdapter.notifyDataSetChanged()

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

            val freeFragment: FreeFragment
            val infoFragment: InfoFragment
            val studyFragment: StudyFragment
            val classFragment: ClassFragment
            val meetingFragment: MeetingFragment
            val couponFragment: CouponFragment
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

        maindata()
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
    }

    fun maindata() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("type",type)

        println("====================================================== type " );

        PostingAction.mainlist(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val list = response.getJSONArray("list")

                        for (i in 0..list.length()-1){



                            adapterData.add(list[i] as JSONObject)
                            println("=============================list"+list[i] as JSONObject)
                        }

                        mainAdapter.notifyDataSetChanged()


                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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
}
