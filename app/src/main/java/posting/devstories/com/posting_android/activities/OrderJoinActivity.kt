package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_orderjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class OrderJoinActivity : RootActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderjoin)

        PostingStartIV.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }



    }

}
