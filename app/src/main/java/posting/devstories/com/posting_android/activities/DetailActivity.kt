package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
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
import posting.devstories.com.posting_android.Actions.PostingAction.detail
import posting.devstories.com.posting_android.Actions.PostingAction.save_posting
import posting.devstories.com.posting_android.Actions.PostingAction.write_comments
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

    val EDIT_POST = 101;
    val STORAGE_POST = 201;
    val USES_COUPON = 301;

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
    var member_id2=-1
    var use_yn:String?= null
    var member_type:String? = null
    var image_uri = ""
    var type = 1
    var contents = ""
    var coupon = -1
    var save_id = -1

    var dlgtype  = ""

    var school_id = -1
    var me_school_id =-1
    var posting_count = -1
    var current_school_id = -1

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
        save_id = intent.getIntExtra("save_id", -1)

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
                    couponLL.visibility = View.GONE
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
            if (save_id < 1){
                dlgView()
            }else{
                storagedlgView()
            }
        }

        /*
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
        */

        submitTV.setOnClickListener {
            var comments = Utils.getString(commentsET)

            if(!comments.isEmpty() && comments != "") {
                writeComments(comments)
            }
        }

        /*
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
        */

        saveLL.setOnClickListener {

            if("N" == confirm_yn) {
                Toast.makeText(context, "학교 인증 후 이용 가능합니다", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(count < 1) {

                Toast.makeText(context, "남은 포스팅 갯수가 없습니다.", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            /*
            val pageCurlView = PageCurlView(context)

            // val bm = ImageLoader.getInstance().loadImageSync(Config.url + image_uri)

            // pageCurlView.setmBackground(bm)

            coupon3RL.setDrawingCacheEnabled(true);
            var bm = coupon3RL.getDrawingCache()
            bm = bm.copy(bm.getConfig(), true)

            pageCurlView.setmForeground(bm)

            // pageCurlView.setmBackground(Utils.createImage(pageCurlViewLL.width, pageCurlViewLL.height, Color.BLACK))

            pageCurlViewLL.addView(pageCurlView)

            coupon3RL.visibility = View.GONE

            pageCurlView.setbFlipping(true)
            pageCurlView.FlipAnimationStep()
            */

            savePosting()

        }

        backLL.setOnClickListener {
            finish()
        }

        detaildata()

    }

    //사용자정보
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

                        var image = Config.url + image_uri
                        ImageLoader.getInstance().displayImage(image,myIV, Utils.UILoptionsUserProfile)

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
    //댓글
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
    //저장
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

                        val count = response.getInt("count")

                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.putExtra("count", count)
                        intent.action = "SAVE_POSTING"
                        sendBroadcast(intent)
//                        detaildata()

                        if(posting_count < 1) {
                            leftCntTV.text = "∞"
                        } else {
                            var leftCnt = posting_count - count;

                            leftCntTV.text = leftCnt.toString()
                        }

                        saveLL.visibility = View.GONE

                    }else if ("empty"==result){
                        Toast.makeText(context,"남은 수량이 없습니다.",Toast.LENGTH_SHORT).show()
                    }else if ("already"==result){
                        Toast.makeText(context,"이미 떼어간 포스트입니다.",Toast.LENGTH_SHORT).show()
                    } else if ("over" == result) {
                        Toast.makeText(context,"오늘 하루 제한량만큼 떼어갔습니다.",Toast.LENGTH_SHORT).show()
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
    //상세뽑기
    fun detaildata() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)
        params.put("del_yn", del_yn)
        //나의학교아이디
        me_school_id  = PrefUtils.getIntPreference(context,"school_id")
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
                        member_id2 =   Utils.getInt(posting, "member_id")

                        var del = Utils.getString(posting,"del_yn")
                        var Image = Utils.getString(posting, "Image")
                        type = Utils.getInt(posting,"type")
                        var menu_name:String =  Utils.getString(posting, "menu_name")
                        var sale_per:String =  Utils.getString(posting, "sale_per")
                        var sale_price:String =  Utils.getString(posting, "sale_price")
                        contents =   Utils.getString(posting, "contents")

                        image_uri = Utils.getString(posting, "image_uri")
                        var leftCount = Utils.getString(posting, "leftCount")
                        var writer_school_id = Utils.getInt(member1, "school_id")
                        PrefUtils.setPreference(context, "detail_current_school_id", writer_school_id)


                        //게시물의 학교아이디
                        school_id = Utils.getInt(posting, "school_id")

                        count = Utils.getInt(posting, "leftCount")
                        posting_count = Utils.getInt(posting, "count")

                        if(count < 1 || me_school_id != school_id || member_id == member_id2 || "Y" == save_yn || "3" == member_type) {

                            println("count : " + count)
                            println("me_school_id : " + me_school_id)
                            println("school_id : " + school_id)
                            println("member_id : " + member_id)
                            println("member_id2 : " + member_id2)
                            println("save_yn : " + save_yn)
                            println("member_type : " + member_type)

                            saveLL.visibility = View.GONE
                        } else {
                            saveLL.visibility = View.VISIBLE
                        }

//                        if (school_id!=me_school_id){
//                            saveLL.visibility = View.GONE
                        if (writer_school_id != school_id){
                            postingLL.background = getDrawable(R.mipmap.write_bg2)
//                            saveLL.visibility = View.GONE
//                        }else if(save_yn.equals("N")&&member_id2!=member_id){
//                                saveLL.visibility = View.VISIBLE
                        }
                        else{
                            postingLL.background = getDrawable(R.mipmap.wtite_bg)
                        }

                        current_school_id = PrefUtils.getIntPreference(context, "current_school_id")
                        if(current_school_id != me_school_id) {
                            // 학교도 다르고 내가 쓴 글이 아니면
                            // 커멘트 막기
                            if (member_id != member_id2) {
                                commentsLL.visibility = View.GONE
                            } else {
                                commentsLL.visibility = View.VISIBLE
                            }
                        } else {
                            commentsLL.visibility = View.VISIBLE
                        }

                        if(6 == type) {

                            val ymd = SimpleDateFormat("yy년MM월dd일", Locale.KOREA)
                            val cymd = SimpleDateFormat("yy.MM.dd", Locale.KOREA)

                            val coupon_startdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uses_start_date)
                            //내용 쿠폰날짜
                            val c_startdate = ymd.format(coupon_startdate)
                            //디테일 이미지에보이는 날짜
                            val ctv_startdate = cymd.format(coupon_startdate)
                            val coupon_enddate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uses_end_date)
                            val c_enddate = ymd.format(coupon_enddate)
                            val ctv_enddate = cymd.format(coupon_enddate)

                            usesTV.visibility = View.VISIBLE
                            usesTV.text = "사용기간:"+c_startdate+" ~ "+c_enddate+" 까지"


                            if (coupon_type.equals("1")){
                                contentsTV.visibility = View.GONE
                                coupon3LL.visibility = View.VISIBLE
                                coupon_titleTV.text = menu_name
                                coupon_saleTV.text = sale_per
                                coupon_orderTV.text = company_name
                                coupon_sale2TV.text = "할인"
                                coupon_TV.text = "%"
                                coupon_startdateTV.text = ctv_startdate+" ~ "
                                coupon_contentTV.text = contents
                                coupon_enddateTV.text = ctv_enddate
                            }else if (coupon_type.equals("2")){
                                contentsTV.visibility = View.GONE
                                coupon3LL.visibility = View.VISIBLE
                                coupon_orderTV.text = company_name
                                coupon3LL.setBackgroundColor(Color.parseColor("#FB2B70"))
                                coupon_titleTV.text = menu_name
                                coupon_saleTV.text = "FREE"
                                coupon_TV.visibility = View.GONE
                                coupon_sale2TV.visibility = View.GONE
                                coupon_startdateTV.text = ctv_startdate+" ~ "
                                coupon_contentTV.text = contents
                                coupon_enddateTV.text = ctv_enddate
                            }else if (coupon_type.equals("3")){
                                contentsTV.visibility = View.GONE
                                coupon3LL.visibility = View.VISIBLE
                                coupon_orderTV.text = company_name
                                coupon3LL.setBackgroundColor(Color.parseColor("#A12BFB"))
                                coupon_titleTV.text = menu_name
                                coupon_saleTV.text = sale_price
                                coupon_sale2TV.text = "할인"
                                coupon_TV.text = "원"
                                coupon_startdateTV.text = ctv_startdate+" ~ "
                                coupon_contentTV.text = contents
                                coupon_enddateTV.text = ctv_enddate
                            }

                            if (use_yn.equals("Y")){
                                couponLL.visibility = View.GONE
                            }else if(save_yn.equals("Y")&&use_yn.equals("N")){

                                if (save_id > 0) {
                                    couponLL.visibility = View.VISIBLE
                                }
                            }
                        }


                        image_uri = Utils.getString(posting, "image_uri")

                        if(count == 9999) {
                            leftCntTV.text = "∞"
                        } else {
                            leftCntTV.text = count.toString()
                        }
//                        var created =   Utils.getString(posting, "created")

                        if (save_id > 0){
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

                            wnameTX.setOnClickListener {
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

                        //uri를 이미지로 변환시켜준다
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            ImageLoader.getInstance().displayImage(image, imgIV, Utils.UILoptionsUserProfile)
                            imgIV.visibility = View.VISIBLE
                        } else {
                            if (!coupon_type.equals("")){
                                contentsTV.visibility=View.GONE
                            }else {
                                contentsTV.text = contents
                                contentsTV.visibility = View.VISIBLE
                            }
                        }

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

    //수정완료
    fun policedlgView(){
        dlgtype = "police"
        var intent = Intent(context, DlgReportActivity::class.java)
        intent.putExtra("posting_id", posting_id)
        intent.putExtra("member_id", member_id)
        intent.putExtra("dlgtype", dlgtype)
        startActivity(intent)
    }

    fun dlgView(){
        dlgtype = "Myposting"
        var intent = Intent(context, DlgReportActivity::class.java)
        intent.putExtra("posting_id", posting_id)
        intent.putExtra("dlgtype", dlgtype)
        intent.putExtra("image_uri",image_uri)
        intent.putExtra("member_type",member_type)
        intent.putExtra("contents",contents)
        intent.putExtra("type",type)
        intent.putExtra("count",posting_count)
        intent.putExtra("school_id",school_id)
        intent.putExtra("current_school_id",current_school_id)
        startActivityForResult(intent, EDIT_POST)

    }
    fun storagedlgView(){
        dlgtype = "Storage"
        var intent = Intent(context, DlgReportActivity::class.java)
        intent.putExtra("save_id", save_id.toString())
        intent.putExtra("dlgtype", dlgtype)
        intent.putExtra("type", type)
        startActivityForResult(intent, STORAGE_POST)

    }

    //다이얼로그수정
    fun coupondlgView(){

        var intent = Intent(context, DlgCouponActivity::class.java)
        intent.putExtra("posting_save_id", posting_save_id)
        startActivityForResult(intent, USES_COUPON)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                EDIT_POST -> {
                    finish()
                }
                STORAGE_POST -> {
                    finish()
                }
                USES_COUPON -> {
                    couponTV.visibility = View.GONE
                }
            }
        }

    }

    override fun finish() {
        super.finish()
        Utils.hideKeyboard(context)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
