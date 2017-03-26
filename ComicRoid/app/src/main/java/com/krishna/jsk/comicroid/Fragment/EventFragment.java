package com.krishna.jsk.comicroid.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.krishna.jsk.comicroid.R;
import com.krishna.jsk.comicroid.adapter.GalleryAdapter;
import com.krishna.jsk.comicroid.app.AppController;
import com.krishna.jsk.comicroid.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {


    private static final String endpoint = "https://gateway.marvel.com/v1/public/events?";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private  String url , hash;
    private  String ts;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_event, container, false);

        Long tsLong = System.currentTimeMillis()/1000;
        ts = tsLong.toString();
        final String toHash = ts + "179e29e3f6447cedbb508940761ff0abc35d1884" + "b136328c81adf02eeb8cbfce3e0ef2a7";
        url = "" + "b136328c81adf02eeb8cbfce3e0ef2a7";
        hash = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashe = md.digest(toHash.getBytes("UTF-8"));
            StringBuffer hex = new StringBuffer(2*hashe.length);
            for (byte b : hashe) {
                hex.append(String.format("%02x", b&0xff));
            }
            hash = hex.toString();
        }
        catch(NoSuchAlgorithmException e) {
        }
        catch(UnsupportedEncodingException e) {
        }
        // Inflate the layout for this fragment
        pDialog = new ProgressDialog(getContext());
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getContext(), images);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_viewev);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();
        return  rootView;
    }
    private void fetchImages() {
        pDialog.setMessage("Getting Events....");
        pDialog.show();

        StringRequest req = new StringRequest (endpoint+"ts="+ts+"&apikey="+url+"&hash="+hash+"&limit=100", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.hide();
                Log.e(getTag() , response);
                images.clear();
                // Convert String to json object
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    JSONObject json_L = json.getJSONObject("data");
                    JSONArray json_LL = json_L.getJSONArray("results");
                    for (int i = 0; i < json_LL.length(); i++) {
                        try {
                            JSONObject object = json_LL.getJSONObject(i);
                            Image image = new Image();
                            image.setName(object.getString("title"));

                            JSONObject url = object.getJSONObject("thumbnail");
                            image.setLarge(url.getString("path")+"."+url.getString("extension"));
                            image.setTimestamp(object.getString("description"));
                            images.add(image);

                        } catch (JSONException e) {
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}
