package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_schooljoin.*
import kotlinx.android.synthetic.main.activity_schooljoin.view.*
import kotlinx.android.synthetic.main.activity_studentjoin.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.SchoolAdapter
import posting.devstories.com.posting_android.base.RootActivity

class SchoolActivity : RootActivity() {



    private var adapterData :ArrayList<JSONObject> = ArrayList<JSONObject>()
    private lateinit var adapter: SchoolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schooljoin)




        nextTX.setOnClickListener {
            val intent = Intent(this,StudentJoinActivity::class.java)
            startActivity(intent)
        }


        var data = JSONObject()
        data.put("school_name", "경기대학교")

        adapterData.add(data)
        adapterData.add(data)
        adapterData.add(data)
        adapterData.add(data)
        adapterData.add(data)

        adapter = SchoolAdapter(this, R.layout.school_item, adapterData)
        SchoolLV.adapter = adapter
        adapter.notifyDataSetChanged()





    }

}
