package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import posting.devstories.com.posting_android.R


open class SpinnerAdapter(context:Context, view:Int, data:ArrayList<String>) : ArrayAdapter<String>(context, view, data) {

    var view:Int = view
    var data:ArrayList<String> = data

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = super.getDropDownView(position, convertView, parent)

        var textTV = view as TextView

        var text = data.get(position)

        textTV.text = text

        if(text == "태어난 연도" || text == "성별") {
            textTV.setTextColor(Color.parseColor("#808080"))
        } else {
            textTV.setTextColor(Color.parseColor("#000000"))
        }

        return view

    }

}