package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_home_view.*
import kotlinx.android.synthetic.main.tab_mypage_view.*
import kotlinx.android.synthetic.main.tab_write_view.*
import posting.devstories.com.posting_android.R

class MainActivity : FragmentActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var context: Context
    private val BACK_PRESSED_TERM = (1000 * 2).toLong()
    private var backPressedTime: Long = 0

    var tabType = 1
    var type = ""

    var member_id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.context = this

        // sliding_tabs.addTab(tabLayout.newTab().setText("Tab 1"));
        // tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        // tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));

        fragmentFT.setup(context, supportFragmentManager, R.id.fragmentFL)

        val tabHomeV = View.inflate(context, R.layout.tab_home_view, null)
        val tabWriteV = View.inflate(context, R.layout.tab_write_view, null)
        val tabMypageV = View.inflate(context, R.layout.tab_mypage_view, null)

        fragmentFT.addTab(fragmentFT.newTabSpec("post").setIndicator(tabHomeV), PostFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("write").setIndicator(tabWriteV), WriteFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("myPage").setIndicator(tabMypageV), MyPageFragment::class.java, null)

        fragmentFT.tabWidget.dividerDrawable = null

//        val fragmentManager = supportFragmentManager
//        var fragment: Fragment = PostFragment()

        homeLL.setOnClickListener {

            setTabBar()


            homeIV.setImageResource(R.mipmap.home)

            fragmentFT.onTabChanged("post")

//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragment = PostFragment()
//            fragmentTransaction.replace(R.id.fragment, fragment)
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit()

        }

        writeLL.setOnClickListener {

            setTabBar()

            writeIV.setImageResource(R.mipmap.clickplus)
            fragmentFT.onTabChanged("write")

//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragment = WriteFragment()
//            fragmentTransaction.replace(R.id.fragment, fragment)
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit()
        }

        myPageLL.setOnClickListener {

            setTabBar()

            myPageIV.setImageResource(R.mipmap.clickmy)
            fragmentFT.onTabChanged("myPage")
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragment = MyPageFragment()
//            fragmentTransaction.replace(R.id.fragment, fragment)
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit()

        }

    }

    fun setTabBar(){
        homeIV.setImageResource(R.mipmap.noclickhome)
        writeIV.setImageResource(R.mipmap.plus)
        myPageIV.setImageResource(R.mipmap.my)
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
