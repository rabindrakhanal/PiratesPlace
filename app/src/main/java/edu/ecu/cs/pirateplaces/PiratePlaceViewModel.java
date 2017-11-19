package edu.ecu.cs.pirateplaces;



import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}

