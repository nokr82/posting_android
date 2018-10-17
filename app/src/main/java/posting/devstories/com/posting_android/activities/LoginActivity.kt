package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_studentjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class LoginActivity : RootActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        JoinStudentIV.setOnClickListener {
            val intent = Intent(this,SchoolActivity::class.java)
            startActivity(intent)
        }
        OrderjoinIV.setOnClickListener {
            val intent = Intent(this, OrderJoinActivity::class.java)
            startActivity(intent)
        }
        StartBT.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        idpwfindTX.setOnClickListener {
            val intent = Intent(this,IDfindActivity::class.java)
            startActivity(intent)
        }







    }

}
