package mytoken.eos.io.class_;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import mytoken.eos.io.Interface.Interface_volley_respose;

import java.util.Map;


/**
 * Created by PC on 2017/1/9.
 */

public class Volley_Utils {
//    DEPRECATED_GET_OR_POST = -1;
//    GET = 0;
//    POST = 1;
//    PUT = 2;
//    DELETE = 3;
//    HEAD = 4;
//    OPTIONS = 5;
//    TRACE = 6;
//    PATCH = 7;

    Interface_volley_respose mInterface;

    RequestQueue mQueue;
    StringRequest mStringRequest;

    public Volley_Utils(Interface_volley_respose mInterface) {
        this.mInterface = mInterface;
    }

    public void Http(String URL, Context mContext, int Method) {
        LogUtils.LOG("ceshi","联网查询的版本号","voll");
        mQueue = Volley.newRequestQueue(mContext);
        mStringRequest = new StringRequest(Method, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtils.LOG("ceshi","正确"+response,"voll");

                if (response != null || response.length() != 0) {
                    mInterface.onSuccesses(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.LOG("ceshi","联网查询的版本号cuowu1111111111111"+"~~e"+error,"voll");


                if (error.networkResponse != null) {
                    int code = error.networkResponse.statusCode;
                    mInterface.onError(code);
                    LogUtils.LOG("ceshi","联网查询的版本号cuowu1111111111111"+"~~e"+error,"voll");

                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };

        mQueue.add(mStringRequest);

    }

    public void postHttp(String URL, Context mContext, int Method, final Map<String, String> map) {
        mQueue = Volley.newRequestQueue(mContext);
        mStringRequest = new StringRequest(Method, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null || response.length() != 0) {
                    mInterface.onSuccesses(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    int code = error.networkResponse.statusCode;
                    mInterface.onError(code);

                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return map;
            }
        };

        mQueue.add(mStringRequest);

    }

//    public void postHttpWithHeader(String URL, Context mContext, int Method, final Map<String, String> map) {
//        mQueue = Volley.newRequestQueue(mContext);
//        mStringRequest = new StringRequest(Method, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (response != null || response.length() != 0) {
//                    mInterface.onSuccesses(response);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse != null) {
//                    int code = error.networkResponse.statusCode;
//                    mInterface.onError(code);
//
//                }
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                return map;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                try {
//                    headers.put("cookie", header);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return headers;
//            }
//        };
//
//        mQueue.add(mStringRequest);
//
//    }
    public void volleyCancle() {
        mStringRequest.cancel();
    }


}
