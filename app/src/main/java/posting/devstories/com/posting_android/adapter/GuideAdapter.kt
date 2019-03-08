package posting.devstories.com.posting_android.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.activities.MainActivity

class GuideAdapter(activity:Activity, imagePaths: ArrayList<String>) : PagerAdapter() {

    private val _activity: Activity = activity
    private val _imagePaths: ArrayList<String> = imagePaths
    private lateinit var inflater: LayoutInflater

    override fun getCount(): Int {
        return this._imagePaths.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val guideIV:ImageView
        val guidestartLL:LinearLayout
        inflater = _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val viewLayout = inflater.inflate(R.layout.fra_guide1, container, false)
        guideIV = viewLayout.findViewById(R.id.guideIV)
        guidestartLL = viewLayout.findViewById(R.id.guidestartLL)


        if (position ==0){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_1)
        }else if (position ==1){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_2)
        }else if (position ==2){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_3)
        }else if (position ==3){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_4)
        }else if (position ==4){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_5)
        }else if (position ==5){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_6)
        }else if (position ==6){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_7)
        }else if (position ==7){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_8)
        }else if (position ==8){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_9)
        }else if (position ==9){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_10)
        }else if (position ==10){
            guidestartLL.visibility = View.GONE
            guideIV.setImageResource(R.mipmap.guide_11)
        }else if (position ==11){
            guidestartLL.visibility = View.VISIBLE
            guideIV.setImageResource(R.mipmap.guide_12)
        }

        guidestartLL.setOnClickListener {
            val intent = Intent(_activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            _activity.startActivity(intent)
        }

        (container as ViewPager).addView(viewLayout)

        return viewLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as RelativeLayout)

    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}