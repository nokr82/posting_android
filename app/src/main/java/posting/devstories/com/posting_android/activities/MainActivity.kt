package posting.devstories.com.posting_android.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.FullScreenImageAdapter
import posting.devstories.com.posting_android.base.Utils
import java.util.ArrayList

class MainActivity : FragmentActivity() {

    lateinit var context: Context

    private val BACK_PRESSED_TERM = (1000 * 2).toLong()
    private var backPressedTime: Long = 0

    lateinit var pagerAdapter: PagerAdapter

    var adverImagePaths = ArrayList<String>()
    private val adverAdapterData = ArrayList<JSONObject>()
    private lateinit var adverAdapter: FullScreenImageAdapter
    var adPosition = 0;

    private var adTime = 0
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.context = this

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

        // 메인 광고 뷰페이저
        adverAdapter = FullScreenImageAdapter(this, adverImagePaths)
        adverVP.adapter = adverAdapter
        adverAdapter.notifyDataSetChanged()
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
        pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerVP.adapter = pagerAdapter
        pagerAdapter.notifyDataSetChanged()

        pagerVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                val intent = Intent()
                intent.action = "MENU_REFRESH"
                intent.putExtra("menu_type", position.toString())
                sendBroadcast(intent)
                when (position) {
                    0 -> {
                        setMenuTabView()
                        freeTX.setTextColor(Color.parseColor("#01b4ec"))
                        freeV.visibility = View.VISIBLE
                    }
                    1 -> {
                        setMenuTabView()

                        infoV.visibility = View.VISIBLE
                        infoTX.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    2 -> {
                        setMenuTabView()

                        StudyV.visibility = View.VISIBLE
                        StudyTX.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    3 -> {
                        setMenuTabView()

                        classV.visibility = View.VISIBLE
                        classTX.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    4 -> {
                        setMenuTabView()

                        mitingV.visibility = View.VISIBLE
                        MitingTX.setTextColor(Color.parseColor("#01b4ec"))
                    }
                    5 -> {
                        setMenuTabView()

                        couponV.visibility = View.VISIBLE
                        CouponTX.setTextColor(Color.parseColor("#01b4ec"))
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
            setMenuTabView()

            freeTX.setTextColor(Color.parseColor("#01b4ec"))
            freeV.visibility = View.VISIBLE

        }

        infoRL.setOnClickListener {
            setMenuTabView()

            infoV.visibility = View.VISIBLE
            infoTX.setTextColor(Color.parseColor("#01b4ec"))
        }

        studyRL.setOnClickListener {
            setMenuTabView()

            StudyV.visibility = View.VISIBLE
            StudyTX.setTextColor(Color.parseColor("#01b4ec"))

        }

        classRL.setOnClickListener {

            setMenuTabView()

            classV.visibility = View.VISIBLE
            classTX.setTextColor(Color.parseColor("#01b4ec"))

        }

        meetingRL.setOnClickListener {

            setMenuTabView()

            mitingV.visibility = View.VISIBLE
            MitingTX.setTextColor(Color.parseColor("#01b4ec"))

        }

        couponRL.setOnClickListener {

            setMenuTabView()

            couponV.visibility = View.VISIBLE
            CouponTX.setTextColor(Color.parseColor("#01b4ec"))
        }



        timer()

    }


    private fun timer() {
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {

                println("test ==================================== " + adTime)

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
        val iv = ImageView(context)
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
            val fragment: Fragment
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
        freeTX.setTextColor(Color.parseColor("#A19F9B"))
        infoTX.setTextColor(Color.parseColor("#A19F9B"))
        StudyTX.setTextColor(Color.parseColor("#A19F9B"))
        classTX.setTextColor(Color.parseColor("#A19F9B"))
        MitingTX.setTextColor(Color.parseColor("#A19F9B"))
        CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        freeV.visibility = View.INVISIBLE
        infoV.visibility = View.INVISIBLE
        StudyV.visibility = View.INVISIBLE
        classV.visibility = View.INVISIBLE
        mitingV.visibility = View.INVISIBLE
        couponV.visibility = View.INVISIBLE

        mainLL.visibility = View.VISIBLE
        pagerVP.visibility = View.GONE
    }

    fun setMenuTabView() {
        freeTX.setTextColor(Color.parseColor("#A19F9B"))
        infoTX.setTextColor(Color.parseColor("#A19F9B"))
        StudyTX.setTextColor(Color.parseColor("#A19F9B"))
        classTX.setTextColor(Color.parseColor("#A19F9B"))
        MitingTX.setTextColor(Color.parseColor("#A19F9B"))
        CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        freeV.visibility = View.INVISIBLE
        infoV.visibility = View.INVISIBLE
        StudyV.visibility = View.INVISIBLE
        classV.visibility = View.INVISIBLE
        mitingV.visibility = View.INVISIBLE
        couponV.visibility = View.INVISIBLE

        mainLL.visibility = View.GONE
        pagerVP.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < BACK_PRESSED_TERM) {
            finish()
        } else {
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }
    }

}
