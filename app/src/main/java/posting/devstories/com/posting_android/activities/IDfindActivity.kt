package posting.devstories.com.posting_android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.idfind_activity.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class IDfindActivity : RootActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.idfind_activity)

        pwfindTX.setOnClickListener {

            getView()
            pwfindV.visibility=View.VISIBLE
            findBT.setText("패스워드 찾기")
        }

        idfindTX.setOnClickListener {

            getView()
            idfindV.visibility = View.VISIBLE
            findBT.setText("아이디 찾기")

        }

        findBT.setOnClickListener {
            val getId:String = idET.text.toString()
            val getName:String = nameET.text.toString()

            if (getId==""||getId==null){
                Toast.makeText(this,"이메일을 입력해주세요", Toast.LENGTH_SHORT).show()

            }else if (getName==""||getName == null){
                Toast.makeText(this,"이름를 입력해주세요", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this,"찾는중...", Toast.LENGTH_SHORT).show()

            }

        }






    }

    fun getView(){
        pwfindV.visibility = View.INVISIBLE
        idfindV.visibility = View.INVISIBLE


    }

}
