package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_back_main.*
import posting.devstories.com.posting_android.R

class backMainActivity : FragmentActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var context: Context
    private val BACK_PRESSED_TERM = (1000 * 2).toLong()
    private var backPressedTime: Long = 0

    lateinit var pagerAdapter: PagerAdapter

    var tabType = 1;
    var type = ""

    var member_id = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_main)

        this.context = this

        pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerVP.adapter = pagerAdapter
        pagerAdapter.notifyDataSetChanged()
        pagerVP.setPagingEnabled(false)
        pagerVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                when (position) {
                    0 -> {
                        setTabBar();
                        homeIV.setImageResource(R.mipmap.home)
                    }
                    1 -> {
                        setTabBar();
                        writeIV.setImageResource(R.mipmap.clickplus)
                    }
                    2 -> {
                        setTabBar();
                        myPageIV.setImageResource(R.mipmap.clickmy)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        homeLL.setOnClickListener {
            pagerVP.currentItem = 0
        }

        writeLL.setOnClickListener {
            pagerVP.currentItem = 1
        }

        myPageLL.setOnClickListener {
            pagerVP.currentItem = 2
        }

    }

    fun setTabBar(){
        homeIV.setImageResource(R.mipmap.noclickhome)
        writeIV.setImageResource(R.mipmap.plus)
        myPageIV.setImageResource(R.mipmap.my)
    }

    class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {

            var fragment: Fragment

            val args = Bundle()
            when (i) {
                0 -> {

                    fragment = PostFragment()
                    fragment.arguments = args

                    return fragment
                }
                1 -> {
                    fragment = WriteFragment()
                    fragment.arguments = args

                    return fragment
                }
                2 -> {
                    fragment = MyPageFragment()
                    fragment.arguments = args
                    return fragment
                }
                else -> {
                    fragment = PostFragment()
                    fragment.arguments = args
                    return fragment
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return ""
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
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
