package posting.devstories.com.posting_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.Actions.PostingAction.del_posting
import posting.devstories.com.posting_android.Actions.PostingAction.detail
import posting.devstories.com.posting_android.Actions.PostingAction.save_posting
import posting.devstories.com.posting_android.Actions.PostingAction.savedel_posting
import posting.devstories.com.posting_android.Actions.PostingAction.write_comments
import posting.devstories.com.posting_android.Actions.ReviewAction
import posting.devstories.com.posting_android.Actions.ReviewAction.report
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.DetailAnimationRecyclerAdapter
import posting.devstories.com.posting_android.adapter.ReAdapter
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import swipeable.com.layoutmanager.OnItemSwiped
import swipeable.com.layoutmanager.SwipeableLayoutManager
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : RootActivity() {
    var nick = ""
    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var member_id = -1
    var posting_save_id = ""
    var posting_id  = ""
    var p_comments_id = -1
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    var count = 0
    var del_yn = ""
    var use_yn:String?= null
    var member_type:String? = null
    var image_uri = ""
    var type = 1
    var contents = ""
    var coupon = -1
    var taptype = -1
    var save_id :String? = null
    var confirm_yn = ""
    lateinit var adapterRe: ReAdapter

    var postingData:JSONObject = JSONObject();

    private lateinit var detailAnimationRecyclerAdapter: DetailAnimationRecyclerAdapter

    private lateinit var detailAnimationRecyclerAdapterData: ArrayList<JSONObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        this.context = this
        progressDialog = ProgressDialog(context)
        loadInfo()

        intent = getIntent()
        taptype=intent.getIntExtra("taptype",-1)
        save_id = intent.getStringExtra("save_id")

        coupon = intent.getIntExtra("coupon",-1)
        use_yn = intent.getStringExtra("use_yn")

        posting_id = intent.getStringExtra("id")

        member_type= PrefUtils.getStringPreference(context,"member_type")
        member_id = PrefUtils.getIntPreference(context, "member_id")

        confirm_yn = PrefUtils.getStringPreference(context, "confirm_yn")

        commentsLV.isExpanded = true
        adapterRe = ReAdapter(context,R.layout.item_re, adapterData)
        commentsLV.adapter = adapterRe
        adapterRe.notifyDataSetChanged()

        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////
        // animation like tinder
        detailAnimationRecyclerAdapterData = ArrayList<JSONObject>()
        detailAnimationRecyclerAdapter = DetailAnimationRecyclerAdapter(detailAnimationRecyclerAdapterData)
        val swipeableTouchHelperCallback = object : SwipeableTouchHelperCallback(object : OnItemSwiped {
            override fun onItemSwiped() {
                detailAnimationRecyclerAdapter.removeTopItem()

                if("N" == confirm_yn) {
                    Toast.makeText(context, "학교 인증 후 이용 가능합니다", Toast.LENGTH_LONG).show()
                    return
                }

                savePosting();
            }

            override fun onItemSwipedLeft() {
                Log.e("SWIPE", "LEFT")
            }

            override fun onItemSwipedRight() {
                Log.e("SWIPE", "RIGHT")
            }

            override fun onItemSwipedUp() {
                Log.e("SWIPE", "UP")
            }

            override fun onItemSwipedDown() {
                Log.e("SWIPE", "DOWN")
            }
        }) {
            override fun getAllowedSwipeDirectionsMovementFlags(viewHolder: RecyclerView.ViewHolder): Int {
                return ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeableTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)

        recycler_view.setLayoutManager(
            SwipeableLayoutManager().setAngle(10)
                .setAnimationDuratuion(450)
                .setMaxShowCount(3)
                .setScaleGap(0.1f)
                .setTransYGap(0)
        )
        recycler_view.setAdapter(detailAnimationRecyclerAdapter)

        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////



        policeTV.setOnClickListener {
            policedlgView()
        }

        commentsLV.setOnItemClickListener { adapterView, view, i, l ->

            var data = adapterData.get(i)
            val comments_id = Utils.getInt(data, "id")
            if (comments_id != -1) {
                p_comments_id = comments_id
                commentsET.requestFocus()
                Utils.showKeyboard(context)
                commentsET.hint = "답글쓰기"
            }
        }

        couponTV.setOnClickListener {
            coupondlgView()
        }


        menuIV.setOnClickListener {
            if (taptype ==2){
                storagedlgView()
            }else{
                dlgView()
            }
            }

        commentsET.setOnEditorActionListener { textView, i, keyEvent ->
            commentsET.hint = "댓글쓰기"
            when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    var comments = Utils.getString(commentsET)

                    if(!comments.isEmpty() && comments != "") {
                        writeComments(comments)

                    }
                }
            }
            return@setOnEditorActionListener true

        }
        postingLL.setOnClickListener {

            if(count < 1) {

                Toast.makeText(context, "남은 포스팅 갯수가 없습니다.", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if("N" == confirm_yn) {
                Toast.makeText(context, "학교 인증 후 이용 가능합니다", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

           savePosting()

        }
        saveLL.setOnClickListener {
            if(count < 1) {

                Toast.makeText(context, "남은 포스팅 갯수가 없습니다.", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if("N" == confirm_yn) {
                Toast.makeText(context, "학교 인증 후 이용 가능합니다", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            savePosting()
        }

        backLL.setOnClickListener {
            finish()
        }

        detaildata()

    }

    fun loadInfo() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

        MemberAction.my_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var member = response.getJSONObject("member")
                        nick =  Utils.getString(member, "nick_name")
                        var image_uri = Utils.getString(member, "image_uri")
                        if (!image_uri.equals("")||image_uri!=null){
                        var image = Config.url + image_uri
                        ImageLoader.getInstance().displayImage(image,myIV, Utils.UILoptionsPosting)
                        }

                        mynameTV.text =nick

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }


            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }




    fun writeComments(comments:String) {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)
        params.put("comments", comments)
        params.put("p_comments_id", p_comments_id)

        write_comments(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {
                        detaildata()
                        Utils.hideKeyboard(context)
                        commentsET.setText("")



                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun use_posting(){
        val params = RequestParams()
        params.put("posting_save_id", posting_save_id)

        PostingAction.use_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {


                        finish()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }


            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    fun savePosting() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)

        save_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var intent = Intent(context, DlgStorageActivity::class.java)
                        startActivity(intent)

                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.action = "SAVE_POSTING"
                        sendBroadcast(intent)

                        saveLL.visibility = View.GONE

                    }else if ("empty"==result){
                        Toast.makeText(context,"남은 수량이 없습니다.",Toast.LENGTH_SHORT).show()

                    }else if ("already"==result){
                        Toast.makeText(context,"이미 떼어간 포스트입니다.",Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {
                    // progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }




    fun detaildata() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)
        params.put("del_yn", del_yn)

        detail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("posting")


                        val posting = data.getJSONObject("Posting")

                        val member1 = data.getJSONObject("Member")
                        var company_name = Utils.getString(member1, "company_name")


                        postingData = posting

//                        var manager = CardStackLayoutManager(context)
//                        var setting:SwipeAnimationSetting  = SwipeAnimationSetting.Builder()
//                            .setDirection(Direction.Right)
//                            .setDuration(200)
//                            .setInterpolator(AccelerateInterpolator())
//                            .build();
//                        manager.setSwipeAnimationSetting(setting)
//                        cardSV.adapter = DetailAdapter(context, postingData)
//                        cardSV.layoutManager = manager
//                        cardSV.swipe()


                        posting_save_id  = Utils.getString(posting,"posting_save_id")

                        val save_yn = Utils.getString(posting,"save_yn")
                        val use_yn = Utils.getString(posting,"use_yn")


                        var uses_start_date = Utils.getString(posting, "uses_start_date")
                        var uses_end_date =   Utils.getString(posting, "uses_end_date")



                        var coupon_type:String =  Utils.getString(posting, "coupon_type")
                        var id = Utils.getString(posting, "id")
                        var member_id2 =   Utils.getInt(posting, "member_id")

                        var del = Utils.getString(posting,"del_yn")
                        var Image = Utils.getString(posting, "Image")
                        type = Utils.getInt(posting,"type")
                        var menu_name:String =  Utils.getString(posting, "menu_name")
                        var sale_per:String =  Utils.getString(posting, "sale_per")
                        var sale_price:String =  Utils.getString(posting, "sale_price")
                        var contents =   Utils.getString(posting, "contents")

                       image_uri = Utils.getString(posting, "image_uri")
                        var leftCount = Utils.getString(posting, "leftCount")



                        if (coupon_type.equals("1")){
                            coupon3RL.visibility = View.VISIBLE
                            coupon_titleTV.text = menu_name
                            coupon_saleTV.text = sale_per
                            coupon_orderTV.text = company_name
                            coupon_sale2TV.text = "할인"
                            coupon_startdateTV.text = uses_start_date
                            coupon_contentTV.text = contents
                            coupon_enddateTV.text = uses_end_date
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }else if (coupon_type.equals("2")){
                            coupon3RL.visibility = View.VISIBLE
                            coupon_orderTV.text = company_name
                            coupon3LL.setBackgroundColor(Color.parseColor("#FB2B70"))
                            coupon_titleTV.text = menu_name
                            coupon_saleTV.text = "FREE"
                            coupon_TV.visibility = View.GONE
                            coupon_sale2TV.visibility = View.GONE
                            coupon_startdateTV.text = uses_start_date
                            coupon_contentTV.text = contents
                            coupon_enddateTV.text = uses_end_date
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }else if (coupon_type.equals("3")){
                            coupon3RL.visibility = View.VISIBLE
                            coupon_orderTV.text = company_name
                            coupon3LL.setBackgroundColor(Color.parseColor("#A12BFB"))
                            coupon_titleTV.text = menu_name
                            coupon_saleTV.text = sale_price
                            coupon_sale2TV.text = "할인"
                            coupon_TV.text = "원"
                            coupon_startdateTV.text = uses_start_date
                            coupon_contentTV.text = contents
                            coupon_enddateTV.text = uses_end_date
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }

                        if (save_yn.equals("N")){
                            saveLL.visibility = View.VISIBLE
                        }



                        if (type ==6&&use_yn.equals("Y")){
                            couponLL.visibility = View.GONE
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }else if(type ==6&&save_yn.equals("Y")&&use_yn.equals("N")){
                            couponLL.visibility = View.VISIBLE
                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+uses_start_date+" ~ "+uses_end_date+" 까지"
                        }


                        image_uri = Utils.getString(posting, "image_uri")
                        count = Utils.getInt(posting, "leftCount")

                        leftCntTV.text = count.toString()
//                        var created =   Utils.getString(posting, "created")

                        if (taptype==2){
                            menuIV.visibility = View.VISIBLE

                        }


                        if (member_id==member_id2){
//                            myLL.visibility = View.VISIBLE
                            menuIV.visibility = View.VISIBLE
                        }

                        val member  = data.getJSONObject("Member")

                        contents =   Utils.getString(posting, "contents")
                        var nick_name = Utils.getString(member, "nick_name")

                        var profile = Config.url + Utils.getString(member,"image_uri")
                        ImageLoader.getInstance().displayImage(profile, writerIV, Utils.UILoptionsUserProfile)

                        if("3" == Utils.getString(member, "member_type")) {
                            writerIV.setOnClickListener {
                                var intent = Intent(context, OrderPageActivity::class.java)
                                intent.putExtra("company_id", Utils.getInt(member, "id"))
                                startActivity(intent)
                            }
                        }

                        val comments = data.getJSONArray("PostingComment")
                        p_comments_id = -1
                        adapterData.clear()
                        for (i in 0..comments.length() - 1) {
                            adapterData.add(comments[i] as JSONObject)

                        }

                        adapterRe.notifyDataSetChanged()

                        contentTV.text = contents
                        wnameTX.text = nick_name

                        //날짜받기
                        val sdf = SimpleDateFormat("MM월dd일", Locale.KOREA)
                        val created = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utils.getString(posting, "created"))
                        val create_date = sdf.format(created)

                        upTX.text = create_date

                        /*
                        //uri를 이미지로 변환시켜준다
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            // ImageLoader.getInstance().displayImage(image, imgIV, Utils.UILoptionsUserProfile)
                            // imgIV.visibility = View.VISIBLE
                        } else {
                            contentsTV.text = contents
                            contentsTV.visibility = View.VISIBLE
                        }
                        */

                        for(idx in 0..count) {
                            detailAnimationRecyclerAdapterData.add(posting)
                        }
                        detailAnimationRecyclerAdapter.notifyDataSetChanged()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun policedlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
        val titleTV = dialogView.findViewById<TextView>(R.id.titleTV)
        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)
        titleTV.text = "이 포스트를 신고하는 이유를 선택하세요"
        delTV.text = "불건전합니다"
        modiTV.text = "부적절합니다"
        recyTV.text = "스팸입니다"

        delTV.setOnClickListener {
            report("1")
            mPopupDlg!!.dismiss()

        }
        modiTV.setOnClickListener {
            report("2")
            mPopupDlg!!.dismiss()
        }
        recyTV.setOnClickListener {
            report("3")
            mPopupDlg!!.dismiss()
        }



        mPopupDlg =  builder.setView(dialogView).show()

    }

    fun coupondlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.coupon_dlg, null)
        val couponnoTX = dialogView.findViewById<TextView>(R.id.couponnoTX)
        val couponyTX = dialogView.findViewById<TextView>(R.id.couponyTX)


        couponnoTX.setOnClickListener {
            mPopupDlg!!.cancel()
        }

        couponyTX.setOnClickListener {

            use_posting()
            mPopupDlg!!.cancel()

        }

        mPopupDlg =  builder.setView(dialogView).show()

    }


    fun report(type:String){
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("posting_id", posting_id)
        params.put("type", type)

        ReviewAction.report(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        var intent = Intent(context, DlgPoliceActivity::class.java)
                        startActivity(intent)

                    } else if("already" == result) {
                        Toast.makeText(context, "신고한 게시물입니다.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun dlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)
        recyTV.visibility = View.GONE

        delTV.setOnClickListener {
            del_posting()
            mPopupDlg!!.cancel()
        }

        modiTV.setOnClickListener {

            val intent = Intent(context, PostWriteActivity::class.java)
            intent.putExtra("posting_id", posting_id)
            intent.putExtra("image_uri",image_uri)
            println("------------dlalwl"+image_uri)
            intent.putExtra("member_type",member_type)
            intent.putExtra("contents",contents)

            context.startActivity(intent)
            finish()

            mPopupDlg!!.cancel()

        }




        mPopupDlg =  builder.setView(dialogView).show()

    }


    fun storagedlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)
        val titleTV = dialogView.findViewById<TextView>(R.id.titleTV)
        titleTV.text = "My Storage"
        recyTV.visibility = View.GONE
        modiTV.visibility = View.GONE

        delTV.setOnClickListener {
            savedel_posting()
            mPopupDlg!!.cancel()
        }



        mPopupDlg =  builder.setView(dialogView).show()

    }


    fun savedel_posting(){
        val params = RequestParams()
        params.put("posting_id", save_id)

        PostingAction.savedel_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.putExtra("type", type)
                        intent.action = "DEL_POSTING"
                        sendBroadcast(intent)

                        finish()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun del_posting(){
        val params = RequestParams()
        params.put("posting_id", posting_id)

    PostingAction.del_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.putExtra("type", type)
                        intent.action = "DEL_POSTING"
                        sendBroadcast(intent)

                        finish()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }
}
