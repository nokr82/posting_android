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
            setView()

            freeTX.setTextColor(Color.parseColor("#01b4ec"))
            freeV.visibility = View.VISIBLE


        }
        infoTX.setOnClickListener {
            setView()
            infoV.visibility = View.VISIBLE
            infoTX.setTextColor(Color.parseColor("#01b4ec"))
        }
        StudyTX.setOnClickListener {
            setView()
            StudyV.visibility = View.VISIBLE
            StudyTX.setTextColor(Color.parseColor("#01b4ec"))

        }
        classTX.setOnClickListener {

            setView()
            classV.visibility = View.VISIBLE
            classTX.setTextColor(Color.parseColor("#01b4ec"))

        }
        MitingTX.setOnClickListener {

            setView()
            mitingV.visibility = View.VISIBLE
            MitingTX.setTextColor(Color.parseColor("#01b4ec"))

        }
        CouponTX.setOnClickListener {

            setView()
            couponV.visibility = View.VISIBLE
            CouponTX.setTextColor(Color.parseColor("#01b4ec"))

        }












    }


    fun setView(){
        freeTX.setTextColor(Color.parseColor("#A19F9B"))
        infoTX.setTextColor(Color.parseColor("#A19F9B"))
        StudyTX.setTextColor(Color.parseColor("#A19F9B"))
        classTX.setTextColor(Color.parseColor("#A19F9B"))
        MitingTX.setTextColor(Color.parseColor("#A19F9B"))
        CouponTX.setTextColor(Color.parseColor("#A19F9B"))
        freeV.visibility = View.INVISIBLE
        infoV.visibility = View.INVISIBLE
        StudyV.visibility = View.INVISIBLE
        classV.visibility = View.INVISIBLE
        mitingV.visibility = View.INVISIBLE
        couponV.visibility = View.INVISIBLE



    }

}
