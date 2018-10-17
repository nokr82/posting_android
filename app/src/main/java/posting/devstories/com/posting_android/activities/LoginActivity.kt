package posting.devstories.com.posting_android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_studentjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.R.id.StartIV
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class LoginActivity : RootActivity() {

    lateinit var context:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.context = this

        JoinStudentIV.setOnClickListener {
            val intent = Intent(this, SchoolActivity::class.java)
            startActivity(intent)
        }

        OrderjoinIV.setOnClickListener {
            val intent = Intent(this, OrderJoinActivity::class.java)
            startActivity(intent)
        }

        StartIV.setOnClickListener {

            var getName = Utils.getString(IDET);
            var getPW = Utils.getString(PWET);

            if (getName == "" || getName == null || getName.isEmpty()) {
                Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getPW == "" || getPW == null || getPW.isEmpty()) {
                Toast.makeText(context, "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                PWET.requestFocus()
                return@setOnClickListener
            }

            if(!Utils.isValidEmail(getName)) {
                Toast.makeText(context, "이메일을 확인해주세요.", Toast.LENGTH_LONG).show();
                IDET.requestFocus()
                return@setOnClickListener
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        idpwfindTX.setOnClickListener {
            val intent = Intent(this, IDfindActivity::class.java)
            startActivity(intent)
        }

    }

}
