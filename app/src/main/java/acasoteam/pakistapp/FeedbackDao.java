package acasoteam.pakistapp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import acasoteam.pakistapp.asynktask.GetFeedback;
import acasoteam.pakistapp.asynktask.SendFeedback;
import acasoteam.pakistapp.asynktask.SendReport;

/**
 * Created by andre on 19/02/2017.
 */
public class FeedbackDao {

    public boolean getFeedback (int idpaki, Context context){

        String u = "https://acaso-pakistapp.rhcloud.com/PakiOperation?action=pakiFeedback&idpaki="+idpaki;
        try {
            new GetFeedback(context,idpaki).execute(u);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }


    public boolean sendFeedback (int idpaki, String feedback, int rate, String loginId, String name, String email, Context context){


        String u = "https://acaso-pakistapp.rhcloud.com/UserOperation?action=addFeedback&comment="+feedback.replace(" ","%20")+"&idPaki="+idpaki+"&score="+rate+"&loginId="+loginId+"&name="+name+"&email="+email;

        Log.v("TEST:",u);

        try {
            new SendFeedback(context).execute(u);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

}