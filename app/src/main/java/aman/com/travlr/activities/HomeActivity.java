package aman.com.travlr.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import aman.com.travlr.R;
import aman.com.travlr.adapters.AdapterExplore;
import aman.com.travlr.application.MyApplication;
import aman.com.travlr.network.VolleySingleton;
import aman.com.travlr.pojo.ExplorePlace;

import static aman.com.travlr.data.Keys.*;


public class HomeActivity extends AppCompatActivity implements AdapterExplore.ClickListener {
    private Toolbar toolbar;
    private TextView embeddedText;
    private TextView splashText;
    private TextView subText;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ArrayList<ExplorePlace> nearbyList = new ArrayList<>();
    private AdapterExplore adapterExplore;
    private RecyclerView venueList;

  /*  @Override
    protected void onStart() {
        super.onStart();
        //get tracker
        Tracker tracker = MyApplication.getInstance().getTracker();
        tracker.setScreenName("Home Activity");
        //Send screen views
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);
        embeddedText = (TextView) findViewById(R.id.embeddedText);
        Typeface embeddedFace = Typeface.createFromAsset(getAssets(), "fonts/embedded_text_font.ttf");
        embeddedText.setTypeface(embeddedFace);
        splashText = (TextView) findViewById(R.id.splash_text);
        Typeface splashFace = Typeface.createFromAsset(getAssets(), "fonts/splash_text_font.ttf");
        splashText.setTypeface(splashFace);
        subText = (TextView) findViewById(R.id.sub_text);
        Typeface subFace = Typeface.createFromAsset(getAssets(), "fonts/font_main.ttf");
        subText.setTypeface(subFace);
        venueList = (RecyclerView) findViewById(R.id.venue_list);
        venueList.setLayoutManager(new LinearLayoutManager(this));
        adapterExplore = new AdapterExplore(this);
        adapterExplore.setClickListener(this);
        venueList.setAdapter(adapterExplore);
        //get json data
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sendJsonRequest();

    }

    private void sendJsonRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, buildUrl(10), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                splashText.setVisibility(View.GONE);
                subText.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                embeddedText.setVisibility(View.VISIBLE);
                nearbyList = parseJsonResponse(response);
                adapterExplore.setExploreList(nearbyList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "No Network Connection.Try Again Later", Toast.LENGTH_LONG).show();
                Log.d("Aman","aman"+error.getLocalizedMessage());
            }
        });
        requestQueue.add(request);

    }

    private ArrayList<ExplorePlace> parseJsonResponse(JSONObject response) {
        ArrayList<ExplorePlace> explorePlaces = new ArrayList<>();
        if (response == null || response.length() == 0) {
            return explorePlaces;
        }
        try {
            JSONArray itemsArray = response.getJSONObject(RESPONSE).getJSONArray(GROUPS).getJSONObject(0).getJSONArray(ITEMS);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject currentItem = itemsArray.getJSONObject(i);
                JSONObject currentVenue = currentItem.getJSONObject(VENUE);
                JSONObject locationObject = currentVenue.getJSONObject(LOCATION);
                JSONArray formattedAddress = locationObject.getJSONArray(FORMATTED_ADDRESS);
                JSONArray categoriesArray = currentVenue.getJSONArray(CATEGORIES);
                JSONObject categoryInfo = categoriesArray.getJSONObject(0);
                JSONObject categoryIcon=categoryInfo.getJSONObject(ICON);
                JSONObject venuePhoto = currentVenue.getJSONObject(PHOTOS).getJSONArray(GROUPS).getJSONObject(0).getJSONArray(ITEMS).getJSONObject(0);

                String tipCount = currentVenue.getJSONObject(STATS).getString(TIP_COUNT);
                String fullAddress = "";
                for (int j = 0; j < formattedAddress.length(); j++) {
                    fullAddress += formattedAddress.getString(j);
                    fullAddress += "\n";
                }

                ExplorePlace place = new ExplorePlace();
                if (currentVenue.has(ID))
                    place.setId(currentVenue.getString(ID));
                place.setName(currentVenue.getString(NAME));
                place.setAddress(locationObject.getString(ADDRESS));
                place.setCity(locationObject.getString(CITY));
                place.setState(locationObject.getString(STATE));
                place.setCountry(locationObject.getString(COUNTRY));
                place.setPostalCode(locationObject.getString(POSTAL_CODE));
                if (currentVenue.has(RATING)){
                    place.setRatings(currentVenue.getString(RATING));
                    place.setRatingColor("#"+currentVenue.getString(RATING_COLOR));
                }
                place.setCategoryName(categoryInfo.getString(CATEGORY_NAME));
                place.setCategoryIconUrl(setupCategoryIconUrl(categoryIcon.getString(PREFIX),categoryIcon.getString(SUFFIX)));
                place.setTipCount(tipCount);
                place.setVenuePhotoUrl(setupVenuePhotoUrl(venuePhoto.getString(PREFIX), venuePhoto.getString(SUFFIX)));
                place.setFormattedAddress(fullAddress);

                explorePlaces.add(place);
            }

        } catch (JSONException e) {
            //Log.d("amam",e.toString());
        }
        return explorePlaces;
    }

    private String setupVenuePhotoUrl(String prefix, String suffix) {
        String resolution = "500x300";
        return prefix + resolution + suffix;
    }
    private String setupCategoryIconUrl(String prefix,String suffix){
        String size="64";
        return prefix+size+suffix;
    }

    private String buildUrl(int limit) {
        String mLimit = Integer.toString(limit);
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(PATH_VENUES)
                .appendPath(PATH_EXPLORE)
                .appendPath("")
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .appendQueryParameter(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .appendQueryParameter(KEY_VERSION, VERSION)
                .appendQueryParameter(KEY_NEAR, VALUE_NEAR)
                .appendQueryParameter(KEY_VENUE_PHOTO, TRUE)
                .appendQueryParameter(KEY_LIMIT, mLimit)
                .build();
        return builtUri.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(this, PlaceDetailsActivity.class);
        intent.putExtra("position", position);
        intent.putParcelableArrayListExtra("list_places", nearbyList);
        startActivity(intent);
    }
}
