package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import java.text.SimpleDateFormat

open class AlarmAdapter(context: Context, view:Int, data : ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        lateinit var retView: View

        if (convertView == null) {
            retView = View.inflate(context, view, null)
            item = ViewHolder(retView)
            retView.tag = item
        } else {
            retView = convertView
            item = convertView.tag as ViewHolder
            if (item == null) {
                retView = View.inflate(context, view, null)
                item = ViewHolder(retView)
                retView.tag = item
            }
        }

        var json = data.get(position)

        var alarm = json.getJSONObject("Alarm")
        var type = Utils.getString(alarm, "type")
        var created = Utils.getString(alarm, "created")

        var member = json.getJSONObject("Member")
        var nick_name = Utils.getString(member, "nick_name")
        var member_profile= Utils.getString(member, "image_uri")

        var posting = json.getJSONObject("Posting")
        var posting_image_uri = Utils.getString(posting, "image_uri")
        var contents = Utils.getString(posting, "contents")

        if("1" == type) {
            // 포스팅 떼는 알람
            item.messageTV.text = nick_name + "님이 회원님의 포스팅을 떼었습니다."
        }

        item.postingIV.visibility = View.GONE
        item.contentsTV.visibility = View.GONE

        var image:String = Config.url + member_profile
        ImageLoader.getInstance().displayImage(image, item.senderIV, Utils.UILoptionsProfile)

        if(!posting_image_uri.isEmpty() && posting_image_uri != "") {
            var image:String = Config.url+posting_image_uri
            ImageLoader.getInstance().displayImage(image, item.postingIV, Utils.UILoptionsPosting)
            item.postingIV.visibility = View.VISIBLE
        } else {
            item.contentsTV.visibility = View.VISIBLE
            item.contentsTV.text = contents
        }

        var createdDt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(created)
        created = SimpleDateFormat("yyyy년MM월dd일").format(createdDt)

        item.createdTV.text = created

        return  retView
    }

    override fun getItem(position: Int): JSONObject {

        return data.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getCount(): Int {

        return data.count()
    }

    fun removeItem(position: Int){
        data.removeAt(position)
        notifyDataSetChanged()

    }

    class ViewHolder(v: View) {
        var contentsTV:TextView
        var messageTV:TextView
        var createdTV:TextView
        var senderIV:CircleImageView
        var postingIV:ImageView

        init {
            contentsTV = v.findViewById(R.id.contentsTV)
            messageTV = v.findViewById(R.id.messageTV)
            createdTV = v.findViewById(R.id.createdTV)
            senderIV = v.findViewById(R.id.senderIV)
            postingIV = v.findViewById(R.id.postingIV)
        }
    }
}