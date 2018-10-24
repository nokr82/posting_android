package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.activities.WriteFragment
import posting.devstories.com.posting_android.base.ImageLoader
import java.util.*


class ImageAdapter(internal var mContext: Context, internal var photoList: ArrayList<PhotoData>, imageLoader: ImageLoader, selected: LinkedList<String>) : BaseAdapter() {

    private var selected = LinkedList<String>()
    private var imageLoader: ImageLoader? = null

    class PhotoData {
        var photoID: Int = 0
        var photoPath: String? = null
        var bucketPhotoName: String? = null
        var orientation: Int = 0
    }

    init {
        this.imageLoader = imageLoader
        this.selected = selected
    }

    override fun getCount(): Int {
        return photoList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = View.inflate(this.mContext, R.layout.write_item, null)
            holder.imageIV = convertView.findViewById(R.id.imageIV) as ImageView
            holder.selectRL = convertView.findViewById(R.id.selectRL) as posting.devstories.com.posting_android.base.RationalRelativeLayout


            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val photo = photoList[position]

        val photoID = photo.photoID


        if (selected.contains(position.toString())) {
            val idx = selected.indexOf(position.toString())


        } else {


        }
        holder.photoPath = photo.photoPath

        holder.imageIV!!.setImageBitmap(imageLoader!!.getImage(photo.photoID, photo.photoPath, photo.orientation))
        return convertView
    }

    inner class ViewHolder {
        var imageIV: ImageView? = null
        var number: String? = null
        var selected: Boolean = false
        var photoPath: String? = null
        var selectedLL: LinearLayout? = null
        var selectRL: posting.devstories.com.posting_android.base.RationalRelativeLayout? = null
    }

    internal inner class ImageItem {

        var id: Int = 0
        var img: Bitmap? = null
    }
}
