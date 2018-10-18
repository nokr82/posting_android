package posting.devstories.com.posting_android.activities

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_orderjoin.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class OrderJoinActivity : RootActivity() {
    lateinit var context: Context


    var store = arrayOf("삼성", "엘지")
    var school2= arrayOf("서울대","인하대","경희대","한양대")
    lateinit var adpater: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderjoin)

        this.context = this



        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,store)

        StoreSP.adapter  = adpater








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



        




        PostingStartIV.setOnClickListener {

            val getOffice:String = Utils.getString(OfficeET)
            val getPW:String = Utils.getString(pwET)
            val getPW2:String = Utils.getString(pw2ET)
            val getStore:String = Utils.getString(StoreET)
            val getCeo:String = Utils.getString(ceoET)
            val getPhone:String = Utils.getString(phoneET)








            if(getOffice==""||getOffice==null|| getOffice.isEmpty()){
                Toast.makeText(context, "사업자등록번호를 입력해주세요", Toast.LENGTH_SHORT).show()
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
            if(getStore==""||getStore==null|| getStore.isEmpty()){
                Toast.makeText(context, "상호명을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(getCeo==""||getCeo==null|| getCeo.isEmpty()){
                Toast.makeText(context, "대표자 성명을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(getPhone==""||getPhone==null|| getPhone.isEmpty()){
                Toast.makeText(context, "휴대폰번호를 입력해주세요", Toast.LENGTH_SHORT).show()
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
        SchoolLL.setOnClickListener {
            val intent = Intent(this,SchoolActivity::class.java)
            startActivity(intent)
        }


    }

}
