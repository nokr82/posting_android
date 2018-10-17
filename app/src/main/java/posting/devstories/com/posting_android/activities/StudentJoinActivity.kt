package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_studentjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class StudentJoinActivity : RootActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentjoin)

        joinDoneIV.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }





    }

}
