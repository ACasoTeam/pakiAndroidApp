package acasoteam.pakistapp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import acasoteam.pakistapp.asynktask.SendReport;
import acasoteam.pakistapp.database.DBHelper;

/**
 * Created by andre on 15/01/2017.
 */
public class ReportDao {

    public boolean sendReport (String loginId, LatLng latLng, String name, String email, Context context){

        String u = "https://acaso-pakistapp.rhcloud.com/UserOperation?action=sendReport&lat="+latLng.latitude+"&lon="+latLng.longitude+"&loginId="+loginId+"&name="+name+"&email="+email;
        try {
            new SendReport(context).execute(u);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }
}