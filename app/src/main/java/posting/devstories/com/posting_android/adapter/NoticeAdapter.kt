package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import kotlinx.android.synthetic.main.item_notice.view.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.R.id.SchoolLV
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.Utils


open class NoticeAdapter(context:Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data

    override fun getView(position: Int, convertView: View?, parent : ViewGroup?): View {

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
        var Notice = json.getJSONObject("Notice")
        var id = Utils.getString(Notice, "id")
        var title =   Utils.getString(Notice, "title")
        var contents = Utils.getString(Notice, "contents")
        var created =   Utils.getString(Notice, "created")
        var del_yn = Utils.getString(Notice, "del_yn")

        item.noticeTV.text = title

        item.createTV.text = created




        item.noticeTV.setOnClickListener {
            item.webWV.visibility = View.VISIBLE
            val url = Config.url + "/notice/view?id=" + id
            item.webWV.loadUrl(url)

        }



//            item.writerTX.text = id+"|"+created+"|"







        return retView
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

        var noticeTV:TextView
        var createTV:TextView
        var webWV:WebView

        init {
            createTV =  v.findViewById(R.id.createTV) as TextView
            noticeTV = v.findViewById(R.id.noticeTV) as TextView
            webWV = v.findViewById(R.id.webWV) as WebView
        }
    }
}