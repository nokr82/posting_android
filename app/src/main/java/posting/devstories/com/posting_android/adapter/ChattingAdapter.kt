package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils
import java.util.ArrayList


open class ChattingAdapter(context: Context, view: Int, data: ArrayList<JSONObject>): ArrayAdapter<JSONObject>(context, view, data) {

    private lateinit var item: ViewHolder
    private var data: ArrayList<JSONObject> = data
    private var view: Int = view

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = View.inflate(context, view, null)
            item = ViewHolder(convertView!!)
            convertView!!.tag = item
        } else {
            item = convertView.tag as ViewHolder
            if (item == null) {
                convertView = View.inflate(context, view, null)
                item = ViewHolder(convertView!!)
                convertView!!.tag = item
            }
        }

        try {
            val json = data[position]

            val chatting = json.getJSONObject("Chatting")
            val member = json.getJSONObject("Member")

            val myId = PrefUtils.getIntPreference(context, "member_id")
            val send_member_id = Utils.getInt(chatting, "send_member_id")
            val contents = Utils.getString(chatting, "contents")
            val created = Utils.getString(chatting, "created")


            println("send_member_id : " + send_member_id)
            println("myId : " + myId)

            if (send_member_id == myId) {
                item.myLL.visibility = View.VISIBLE
                item.otherLL.visibility = View.GONE

                item.myTV.text = contents

            } else {
                item.myLL.visibility = View.GONE
                item.otherLL.visibility = View.VISIBLE

                // image
                val image_uri = Utils.getString(member, "image_uri")
                ImageLoader.getInstance().displayImage(Config.url + image_uri, item.otherIV, Utils.UILoptionsProfile)

                item.otherTV.text = contents

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }



        return convertView
    }


    class ViewHolder(v: View) {
        var myLL: LinearLayout
        var otherLL: LinearLayout
        var otherIV: CircleImageView
        var otherTV:TextView
        var myTV:TextView

        init {
            myLL = v.findViewById(R.id.myLL) as LinearLayout
            otherLL = v.findViewById(R.id.otherLL) as LinearLayout
            otherIV = v.findViewById(R.id.otherIV) as CircleImageView
            otherTV = v.findViewById(R.id.otherTV) as TextView
            myTV = v.findViewById(R.id.myTV) as TextView

        }
    }

}