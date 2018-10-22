package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Utils


open class ChatAdapter(context: Context,item : Array<String>) : BaseAdapter(){
    private val mContext = context
    private val mItem = item

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder : ViewHolder
        var view = convertView
        if (view == null){
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat,parent,false)
            viewHolder.textView = view.findViewById(R.id.textView)
            view.tag = viewHolder
            viewHolder.textView.text = mItem[position]


            return view
        }else{
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.textView.text = mItem[position]
        return  view
    }

    override fun getItem(position: Int) = mItem[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem.size

    inner class ViewHolder{
        lateinit var textView : TextView
    }
}