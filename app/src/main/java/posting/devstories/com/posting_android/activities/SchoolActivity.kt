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
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class SchoolActivity : RootActivity() {






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schooljoin)




        nextTX.setOnClickListener {
            val intent = Intent(this,StudentJoinActivity::class.java)
            startActivity(intent)
        }









    }

}
