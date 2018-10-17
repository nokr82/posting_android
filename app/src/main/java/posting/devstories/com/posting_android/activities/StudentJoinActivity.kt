package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_studentjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class StudentJoinActivity : RootActivity() {

    var gender = arrayOf("남", "여")
    var years:ArrayList<String> = ArrayList<String>()
    lateinit var adpater:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentjoin)



        joinDoneIV.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        finishLL.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }


        for (i in 1968..2018) {

            years.add(i.toString() + " 년")

        }
        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,years)

        birthSP.adapter  = adpater



        adpater = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender)
        genderSP.adapter = adpater




    }

}
