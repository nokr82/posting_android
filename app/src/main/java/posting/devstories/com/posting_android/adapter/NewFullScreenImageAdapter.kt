package posting.devstories.com.posting_android.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.Utils

class NewFullScreenImageAdapter(activity:Activity, imagePaths: ArrayList<JSONObject>) : PagerAdapter() {

    private val _activity: Activity = activity
    private val _imagePaths: ArrayList<JSONObject> = imagePaths
    private lateinit var inflater: LayoutInflater

    override fun getCount(): Int {
        return this._imagePaths.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgDisplay: ImageView

        inflater = _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false)

        imgDisplay = viewLayout.findViewById(R.id.imgDisplay)
        imgDisplay.scaleType = ImageView.ScaleType.CENTER_CROP

        var json = _imagePaths.get(position)
        var advertise = json.getJSONObject("Advertise")

        imgDisplay.setOnClickListener {

            var link = Utils.getString(advertise, "link")

            val uri = Uri.parse(link)

            var intent = Intent(Intent.ACTION_VIEW, uri)
            _activity.startActivity(intent)

        }

        ImageLoader.getInstance().displayImage(Config.url + Utils.getString(advertise, "image_uri"), imgDisplay, Utils.UILoptionsAder)

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