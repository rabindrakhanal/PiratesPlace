package edu.ecu.cs.pirateplaces;



import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by RabindraK on 11/18/2017.
 */



public class PiratePlaceViewModel extends BaseObservable{

    private PirateBase mPirateBase;
    private PiratePlace mPiratePlace;

    public PiratePlaceViewModel(PirateBase pirateBase){
        mPirateBase = pirateBase;
    }


    public void setPiratePlace(PiratePlace piratePlace){
        mPiratePlace = piratePlace;
        notifyChange();
    }

    @Bindable
    public String getPlaceName(){
        return mPiratePlace.getPlaceName();
    }

    @Bindable
    public String getLastVisited(){
        return mPiratePlace.getLastVisited().toString();
    }

    public void setPlaceName(String name){
        mPiratePlace.setPlaceName(name);
    }

    public void setLastVisited(String lastVisited){
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        try{
                mPiratePlace.setLastVisited(dateFormat.parse(lastVisited));
            } catch (ParseException e) {
                e.printStackTrace();
        }
    }

    public void updatePiratePlace(){
        mPirateBase.updatePiratePlace(mPiratePlace);
    }

    public File getImageUrl(){
        List<File> files = mPirateBase.getPhotoFiles(mPiratePlace);
        if(files.size() != 0) {
            File image = files.get(0);
            return image;
        }
        return null;
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView view, File file){
        if(file != null)
            Picasso.with(view.getContext()).load(file).fit().centerCrop().into(view);
    }
}

