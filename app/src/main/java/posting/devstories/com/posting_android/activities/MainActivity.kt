package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_studentjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class MainActivity : RootActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        freeTX.setOnClickListener {
            freeTX.setTextColor(Color.parseColor("#01b4ec"))
            infoTX.setTextColor(Color.parseColor("#A19F9B"))
            StudyTX.setTextColor(Color.parseColor("#A19F9B"))
            classTX.setTextColor(Color.parseColor("#A19F9B"))
            MitingTX.setTextColor(Color.parseColor("#A19F9B"))
            CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        }
        infoTX.setOnClickListener {
            freeTX.setTextColor(Color.parseColor("#A19F9B"))
            infoTX.setTextColor(Color.parseColor("#01b4ec"))
            StudyTX.setTextColor(Color.parseColor("#A19F9B"))
            classTX.setTextColor(Color.parseColor("#A19F9B"))
            MitingTX.setTextColor(Color.parseColor("#A19F9B"))
            CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        }
        StudyTX.setOnClickListener {
            freeTX.setTextColor(Color.parseColor("#A19F9B"))
            infoTX.setTextColor(Color.parseColor("#A19F9B"))
            StudyTX.setTextColor(Color.parseColor("#01b4ec"))
            classTX.setTextColor(Color.parseColor("#A19F9B"))
            MitingTX.setTextColor(Color.parseColor("#A19F9B"))
            CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        }
        classTX.setOnClickListener {
            freeTX.setTextColor(Color.parseColor("#A19F9B"))
            infoTX.setTextColor(Color.parseColor("#A19F9B"))
            StudyTX.setTextColor(Color.parseColor("#A19F9B"))
            classTX.setTextColor(Color.parseColor("#01b4ec"))
            MitingTX.setTextColor(Color.parseColor("#A19F9B"))
            CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        }
        MitingTX.setOnClickListener {
            freeTX.setTextColor(Color.parseColor("#A19F9B"))
            infoTX.setTextColor(Color.parseColor("#A19F9B"))
            StudyTX.setTextColor(Color.parseColor("#A19F9B"))
            classTX.setTextColor(Color.parseColor("#A19F9B"))
            MitingTX.setTextColor(Color.parseColor("#01b4ec"))
            CouponTX.setTextColor(Color.parseColor("#A19F9B"))

        }
        CouponTX.setOnClickListener {
            freeTX.setTextColor(Color.parseColor("#A19F9B"))
            infoTX.setTextColor(Color.parseColor("#A19F9B"))
            StudyTX.setTextColor(Color.parseColor("#A19F9B"))
            classTX.setTextColor(Color.parseColor("#A19F9B"))
            MitingTX.setTextColor(Color.parseColor("#A19F9B"))
            CouponTX.setTextColor(Color.parseColor("#01b4ec"))

        }












    }

}
