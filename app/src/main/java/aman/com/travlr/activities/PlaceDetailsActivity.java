package aman.com.travlr.activities;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import aman.com.travlr.R;
import aman.com.travlr.network.VolleySingleton;
import aman.com.travlr.pojo.ExplorePlace;

import static aman.com.travlr.data.Keys.BASE_URL;
import static aman.com.travlr.data.Keys.CLIENT_ID;
import static aman.com.travlr.data.Keys.CLIENT_SECRET;
import static aman.com.travlr.data.Keys.ITEMS;
import static aman.com.travlr.data.Keys.KEY_CLIENT_ID;
import static aman.com.travlr.data.Keys.KEY_CLIENT_SECRET;
import static aman.com.travlr.data.Keys.KEY_LIMIT;
import static aman.com.travlr.data.Keys.KEY_NEAR;
import static aman.com.travlr.data.Keys.KEY_PHOTOS;
import static aman.com.travlr.data.Keys.KEY_VENUE_PHOTO;
import static aman.com.travlr.data.Keys.KEY_VERSION;
import static aman.com.travlr.data.Keys.PATH_EXPLORE;
import static aman.com.travlr.data.Keys.PATH_VENUES;
import static aman.com.travlr.data.Keys.PREFIX;
import static aman.com.travlr.data.Keys.RESPONSE;
import static aman.com.travlr.data.Keys.SUFFIX;
import static aman.com.travlr.data.Keys.TRUE;
import static aman.com.travlr.data.Keys.VALUE_NEAR;
import static aman.com.travlr.data.Keys.VERSION;

public class PlaceDetailsActivity extends AppCompatActivity {
    private int pos;
    private ArrayList<ExplorePlace> retrievedList;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imageView;
    private TextView placeName,location,area,country;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private TextView cardTitle;
    private ArrayList<String> photoUrls=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        pos=getIntent().getIntExtra("position", 0);
        retrievedList=getIntent().getParcelableArrayListExtra("list_places");
        toolbar= (Toolbar) findViewById(R.id.titleBar);
        setSupportActionBar(toolbar);
        collapsingToolbar= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(retrievedList.get(pos).getName());
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        imageView= (ImageView) findViewById(R.id.animated_image);
        Typeface text_font=Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Light.otf");
        placeName= (TextView) findViewById(R.id.place_name);
        location= (TextView) findViewById(R.id.location);
        location.setTypeface(text_font);
        area= (TextView) findViewById(R.id.area);
        area.setTypeface(text_font);
        country= (TextView) findViewById(R.id.country);
        country.setTypeface(text_font);
        placeName.setText(retrievedList.get(pos).getName());
        location.setText(retrievedList.get(pos).getAddress());
        area.setText(retrievedList.get(pos).getCity()+" "+retrievedList.get(pos).getPostalCode());
        country.setText(retrievedList.get(pos).getState()+" "+retrievedList.get(pos).getCountry());
        cardTitle= (TextView) findViewById(R.id.card_title);
        Typeface title_card=Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Regular.otf");
        cardTitle.setTypeface(title_card);
        placeName.setTypeface(title_card);
        volleySingleton=VolleySingleton.getInstance();
        requestQueue=volleySingleton.getRequestQueue();
        imageLoader=volleySingleton.getImageLoader();
        sendJsonRequest();
        imageLoader.get(retrievedList.get(pos).getVenuePhotoUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
               // imageView.setImageBitmap(response.getBitmap());
                Palette.from(response.getBitmap()).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        imageView.setImageBitmap(response.getBitmap());
                        int defaultColor = getResources().getColor(android.R.color.black);
                        collapsingToolbar.setContentScrimColor(palette.getDarkMutedColor(defaultColor));;
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, buildUri(4), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                photoUrls=parseJsonResponse(response);


                new Runnable(){
                    int current=0;
                    int interval=5000;
                    @Override
                    public void run() {
                        current++;
                        if(current==photoUrls.size()){
                            current=0;
                        }
                        String url=photoUrls.get(current);
                        imageLoader.get(url, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                imageView.setImageBitmap(response.getBitmap());
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        imageView.postDelayed(this,interval);
                    }
                }.run();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }
    private ArrayList<String> parseJsonResponse(JSONObject response){
        ArrayList<String> urls=new ArrayList<>();
        if(response==null ||response.length()==0)
            return urls;
        try {
            JSONObject photos=response.getJSONObject(RESPONSE).getJSONObject(KEY_PHOTOS);
            JSONArray photosArray=photos.getJSONArray(ITEMS);
            for(int i=0;i<photosArray.length();i++){
                String url = setupPhotoUrl(photosArray.getJSONObject(i).getString(PREFIX),photosArray.getJSONObject(i).getString(SUFFIX));
                urls.add(url);
            }
        } catch (JSONException e) {

        }
        return urls;
    }
    private String setupPhotoUrl(String prefix, String suffix) {
        String resolution = "400x400";
        return prefix + resolution + suffix;
    }

    private String buildUri(int limit){
        String mLimit = Integer.toString(limit);
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(PATH_VENUES)
                .appendPath(retrievedList.get(pos).getId())
                .appendPath(KEY_PHOTOS)
                .appendPath("")
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .appendQueryParameter(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .appendQueryParameter(KEY_VERSION, VERSION)
                .appendQueryParameter(KEY_LIMIT, mLimit)
                .build();
        return builtUri.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
