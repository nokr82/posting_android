package posting.devstories.com.posting_android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_studentjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class StudentJoinActivity : RootActivity() {
    lateinit var context: Context

    var gender = arrayOf("남", "여")
    var years:ArrayList<String> = ArrayList<String>()
    lateinit var adpater:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentjoin)

        this.context = this



        allCK.setOnCheckedChangeListener{
            compoundButton, b ->
            if (b==true){
                serviceCK.isChecked=true
                soloCK.isChecked=true
            }else{
                serviceCK.isChecked=false
                soloCK.isChecked=false
            }
        }




        joinDoneIV.setOnClickListener {
            val getid:String = Utils.getString(idET)
            val getPW:String = Utils.getString(pwET)
            val getPW2:String = Utils.getString(pw2ET)
            val getNick:String = Utils.getString(nickET)
            val getName:String = Utils.getString(nameET)




            if(getid==""||getid==null|| getid.isEmpty()){
                Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(getPW==""||getPW==null|| getPW.isEmpty()){
                Toast.makeText(context, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(getPW!=getPW2){
                Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(getNick==""||getNick==null|| getNick.isEmpty()){
                Toast.makeText(context, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(getName==""||getName==null|| getName.isEmpty()){
                Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (allCK.isChecked!=true){
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (serviceCK.isChecked!=true){
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (soloCK.isChecked!=true){
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }




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
