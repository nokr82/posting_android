package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MainFragment : Fragment() {
    private var progressDialog: ProgressDialog? = null

    lateinit var activity: MainActivity
    var tabType = 1;
    var member_id = -1
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterMain: PostAdapter

    lateinit var gideGV: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        progressDialog = ProgressDialog(context)

        return inflater.inflate(R.layout.fra_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gideGV = view.findViewById<GridView>(R.id.gideGV)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity
        tabType = activity.tabType

        adapterMain = PostAdapter(activity, R.layout.item_post, adapterData)
        gideGV.adapter = adapterMain
        member_id = PrefUtils.getIntPreference(context, "member_id")

        gideGV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            try {
                val Posting = adapterData[position].getJSONObject("Posting")

                //                    Intent intent = new Intent(context, _StoreDetailActivity.class);
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", Utils.getString(Posting, "id"))
                startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }








    }

    fun loadData(type: Int) {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("type", type)

        println("====================================================== type " + type);

        PostingAction.view(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONArray("list")

                        for (i in 0..data.length()-1){

                            println("data[i] : " + data[i])

                            adapterData.add(data[i] as JSONObject)

                        }

                        adapterMain.notifyDataSetChanged()


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

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
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
}
