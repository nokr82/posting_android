package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.RelativeLayout
import android.widget.TextView
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.NonSwipeableViewPager

open class OldMyPagePostingStorageFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null
    lateinit var activity: MainActivity
    var member_id = -1
    var tabType = 1
    var tab = 1

    lateinit var free2TV:TextView
    lateinit var info2TV:TextView
    lateinit var study2TV:TextView
    lateinit var class2TV:TextView
    lateinit var meeting2TV:TextView
    lateinit var coupon2TV:TextView

    lateinit var storageGV: GridView
    lateinit var free2V:View
    lateinit var info2V:View
    lateinit var study2V:View
    lateinit var class2V:View
    lateinit var meeting2V:View
    lateinit var coupon2V:View

    lateinit var free2RL: RelativeLayout
    lateinit var info2RL: RelativeLayout
    lateinit var study2RL: RelativeLayout
    lateinit var class2RL: RelativeLayout
    lateinit var meeting2RL: RelativeLayout
    lateinit var coupon2RL: RelativeLayout

    lateinit var pagerVP:NonSwipeableViewPager
    lateinit var pagerAdapter:PagerAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        val filter3 = IntentFilter("SET_VIEW")

        try {
            if (setViewReceiver != null) {
                getActivity()!!.unregisterReceiver(setViewReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        getActivity()!!.registerReceiver(setViewReceiver, filter3)

        progressDialog = ProgressDialog(myContext)

        return inflater.inflate(R.layout.fra_my_page_posting_storage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        free2TV = view.findViewById(R.id.free2TX)
        info2TV = view.findViewById(R.id.info2TX)
        study2TV = view.findViewById(R.id.Study2TX)
        class2TV = view.findViewById(R.id.class2TX)
        meeting2TV = view.findViewById(R.id.Miting2TX)
        coupon2TV = view.findViewById(R.id.Coupon2TX)

        free2V = view.findViewById(R.id.free2V)
        info2V = view.findViewById(R.id.info2V)
        study2V = view.findViewById(R.id.Study2V)
        class2V = view.findViewById(R.id.class2V)
        meeting2V = view.findViewById(R.id.miting2V)
        coupon2V = view.findViewById(R.id.coupon2V)

        free2RL = view.findViewById(R.id.free2RL)
        info2RL = view.findViewById(R.id.info2RL)
        study2RL = view.findViewById(R.id.study2RL)
        class2RL = view.findViewById(R.id.class2RL)
        meeting2RL = view.findViewById(R.id.meeting2RL)
        coupon2RL = view.findViewById(R.id.coupon2RL)

        storageGV = view.findViewById(R.id.storageGV)

        pagerVP = view.findViewById(R.id.pagerVP)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity

        free2RL.setOnClickListener {
//            adapterData.clear()
//            tabType = 1;
//            setMenuTabView()
            pagerVP.currentItem = 0

        }

        info2RL.setOnClickListener {
//            adapterData.clear()
//            tabType = 2;
//            setMenuTabView()
            pagerVP.currentItem = 1

        }

        study2RL.setOnClickListener {
//            adapterData.clear()
//            tabType = 3;
//            setMenuTabView()
            pagerVP.currentItem = 2
        }

        class2RL.setOnClickListener {
//            adapterData.clear()
//            tabType = 4;
//            setMenuTabView()
            pagerVP.currentItem = 3
        }

        meeting2RL.setOnClickListener {
//            adapterData.clear()
//            tabType = 5;
//            setMenuTabView()
            pagerVP.currentItem = 4
        }

        coupon2RL.setOnClickListener {
//            adapterData.clear()
//            tabType = 6;
//            setMenuTabView()
            pagerVP.currentItem = 5
        }


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

    }

    class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        var tab = 1

        fun setTabType(setTab: Int){
            tab = setTab
        }

        override fun getItem(i: Int): Fragment {

            var fragment: Fragment

            val args = Bundle()
            when (i) {
                0 -> {
                    fragment = MyPageFreeFragment()
                    args.putInt("tab", tab)
                    fragment.arguments = args

                    return fragment
                }
                1 -> {
                    fragment = MyPageInfoFragment()
                    args.putInt("tab", tab)
                    fragment.arguments = args

                    return fragment
                }
                2 -> {
                    fragment = MyPageStudyFragment()
                    args.putInt("tab", tab)
                    fragment.arguments = args
                    return fragment
                }
                3 -> {
                    fragment = MyPageClassFragment()
                    args.putInt("tab", tab)
                    fragment.arguments = args
                    return fragment
                }
                4 -> {
                    fragment = MyPageMeetingFragment()
                    args.putInt("tab", tab)
                    fragment.arguments = args
                    return fragment
                }
                5 -> {
                    fragment = MyPageCouponFragment()
                    args.putInt("tab", tab)
                    fragment.arguments = args
                    return fragment
                }
                else -> {
                    fragment = MyPageFreeFragment()
                    args.putInt("tab", tab)
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

    fun setMenuTabView() {
        free2TV.setTextColor(Color.parseColor("#A19F9B"))
        info2TV.setTextColor(Color.parseColor("#A19F9B"))
        study2TV.setTextColor(Color.parseColor("#A19F9B"))
        class2TV.setTextColor(Color.parseColor("#A19F9B"))
        meeting2TV.setTextColor(Color.parseColor("#A19F9B"))
        coupon2TV.setTextColor(Color.parseColor("#A19F9B"))

        free2V.visibility = View.INVISIBLE
        info2V.visibility = View.INVISIBLE
        study2V.visibility = View.INVISIBLE
        class2V.visibility = View.INVISIBLE
        meeting2V.visibility = View.INVISIBLE
        coupon2V.visibility = View.INVISIBLE

        if(tabType == 1) {
            free2TV.setTextColor(Color.parseColor("#063588"))
            free2V.visibility = View.VISIBLE
        } else if (tabType == 2) {
            info2V.visibility = View.VISIBLE
            info2TV.setTextColor(Color.parseColor("#063588"))
        } else if (tabType == 3) {
            study2V.visibility = View.VISIBLE
            study2TV.setTextColor(Color.parseColor("#063588"))
        } else if (tabType == 4) {
            class2V.visibility = View.VISIBLE
            class2TV.setTextColor(Color.parseColor("#063588"))
        } else if (tabType == 5) {
            meeting2V.visibility = View.VISIBLE
            meeting2TV.setTextColor(Color.parseColor("#063588"))
        } else if (tabType == 6) {
            coupon2V.visibility = View.VISIBLE
            coupon2TV.setTextColor(Color.parseColor("#063588"))
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }

}


