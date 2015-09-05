package aman.com.travlr.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import aman.com.travlr.R;
import aman.com.travlr.activities.HomeActivity;
import aman.com.travlr.application.MyApplication;
import aman.com.travlr.network.VolleySingleton;
import aman.com.travlr.pojo.ExplorePlace;

/**
 * Created by Aman on 21-08-2015.
 */
public class AdapterExplore extends RecyclerView.Adapter<AdapterExplore.ViewHolderPlaces>{
    private ArrayList<ExplorePlace> exploreList = new ArrayList<>();
    private LayoutInflater inflater;
    private ClickListener clickListener;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    public AdapterExplore(Context context){
        inflater=LayoutInflater.from(context);
        volleySingleton=VolleySingleton.getInstance();
        imageLoader=volleySingleton.getImageLoader();
    }
    public void setExploreList(ArrayList<ExplorePlace> exploreList){
        this.exploreList=exploreList;
        notifyItemRangeChanged(0,exploreList.size());
    }
    @Override
    public ViewHolderPlaces onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.venue_card, parent, false);
        ViewHolderPlaces viewHolder=new ViewHolderPlaces(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderPlaces holder, int position) {
        ExplorePlace currentPlace=exploreList.get(position);
        holder.venueName.setText(currentPlace.getName());
        holder.venueAddress.setText(currentPlace.getFullAddress());
        holder.categoryName.setText(currentPlace.getCategoryName());
        holder.ratings.setText(currentPlace.getRatings());
        holder.tipCount.setText(currentPlace.getTipCount()+" "+"Reviews");
        //loading image
        String photoUrl=currentPlace.getVenuePhotoUrl();
        if(photoUrl!=null){
            imageLoader.get(photoUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.venueImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return exploreList.size();
    }

     class ViewHolderPlaces extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView venueImage;
        private TextView venueName;
        private TextView venueAddress;
        private TextView ratings;
        private TextView categoryName;
        private TextView tipCount;
        public ViewHolderPlaces(View itemView) {
            super(itemView);
            venueImage= (ImageView) itemView.findViewById(R.id.venue_image);
            venueImage.setOnClickListener(this);
            venueName= (TextView) itemView.findViewById(R.id.venue_name);
            Typeface venueNameFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/JosefinSlab-Bold.ttf");
            venueName.setTypeface(venueNameFace);
            venueAddress= (TextView) itemView.findViewById(R.id.address);
            ratings= (TextView) itemView.findViewById(R.id.rating);
            categoryName= (TextView) itemView.findViewById(R.id.category);
            tipCount= (TextView) itemView.findViewById(R.id.tip_count);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v,getLayoutPosition());
            }
        }
    }
    public interface ClickListener{
        public void itemClicked(View view,int position);
    }
}
