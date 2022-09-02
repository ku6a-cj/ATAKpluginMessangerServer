
package com.atakmap.android.plugintemplate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.atakmap.android.icons.UserIcon;
import com.atakmap.android.maps.Marker;
import com.atakmap.android.user.PlacePointTool;
import com.atakmap.android.util.SimpleItemSelectedListener;
import com.atakmap.coremap.maps.coords.GeoPoint;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.plugintemplate.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.atakmap.coremap.log.Log;
import com.atakmap.map.layer.feature.Feature;

import java.util.UUID;

public class PluginTemplateDropDownReceiver extends DropDownReceiver implements
        OnStateListener {


    public static final String TAG = PluginTemplateDropDownReceiver.class
            .getSimpleName();

    public static final String SHOW_PLUGIN = "com.atakmap.android.plugintemplate.SHOW_PLUGIN";
    private final View templateView;
    private final Context pluginContext;
    private final View myFirstFragment;
    private final View helloView = null;

    /**************************** CONSTRUCTOR *****************************/

    public PluginTemplateDropDownReceiver(final MapView mapView,
            final Context context) {
        super(mapView);
        this.pluginContext = context;


        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        templateView = PluginLayoutInflater.inflate(context,
                R.layout.main_layout, null);
        myFirstFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.fragment_plugin_main, null);


        final String[] selectLista = new String[1];

        Button addAnAircraft = myFirstFragment.findViewById(R.id.button2);
        final Button addAnAircraft1 = myFirstFragment.findViewById(R.id.button2);

        addAnAircraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAircraftWithRotation();
            }
        });


        Button addAnAtackAircraft = myFirstFragment.findViewById(R.id.button3);
        final Button addAnAtackAircraft1 = myFirstFragment.findViewById(R.id.button3);

        addAnAtackAircraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAtackAircraftWithRotation();
            }
        });


        //implementacja po up menu
     //   final Spinner spinner = helloView
     //           .findViewById(R.id.spinner1);

     //   spinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
     //       @Override
     //       public void onItemSelected(AdapterView<?> parent,
      //                                 int position, long id) {
      //          if (view instanceof TextView)
      //              ((TextView) view).setTextColor(Color.WHITE);
     //       }
     //   });
    //    spinner.setSelection(0);

    }





    /**************************** PUBLIC METHODS *****************************/

    public void disposeImpl() {

    }

    /**************************** INHERITED METHODS *****************************/

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_PLUGIN)) {

            Log.d(TAG, "showing plugin drop down");
            showDropDown(myFirstFragment, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false);
        }
    }



    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }

String CallString= "BlackWidow";
String TypeString= "Aircraft";
int numberOfIteration = 0;


    public void createAircraftWithRotation() {
        PlacePointTool.MarkerCreator mc = new PlacePointTool.MarkerCreator(
                getMapView().getPointWithElevation());
        mc.setUid(UUID.randomUUID().toString());
        mc.setCallsign(String.valueOf(CallString)+" "+numberOfIteration);
        numberOfIteration=numberOfIteration+1;
        mc.setType(String.valueOf(TypeString));
        mc.showCotDetails(false);
        mc.setNeverPersist(true);
        Marker m = mc.placePoint();
        TextView Callsign = myFirstFragment.findViewById(R.id.textView2);
        TextView Type = myFirstFragment.findViewById(R.id.textView1);
        Callsign.setText(CallString+" "+numberOfIteration);
        Type.setText(TypeString);
        // the stle of the marker is by default set to show an arrow, this will allow for full
        // rotation.   You need to enable the heading mask as well as the noarrow mask
        m.setStyle(m.getStyle()
                | Marker.STYLE_ROTATE_HEADING_MASK
                | Marker.STYLE_ROTATE_HEADING_NOARROW_MASK);
        m.setTrack(310, 20);
        m.setMetaInteger("color", Color.BLACK);
        m.setMetaString(UserIcon.IconsetPath, "34ae1613-9645-4222-a9d2-e5f243dea2865/Military/F22.png");
        //
        m.refresh(getMapView().getMapEventDispatcher(), null,
                this.getClass());

    }

    String CallString2= "Riper";
    String TypeString2= "Atack Aircraft";
    int numberOfIteration2 = 0;

    public void createAtackAircraftWithRotation() {
        PlacePointTool.MarkerCreator mc = new PlacePointTool.MarkerCreator(
                getMapView().getPointWithElevation());
        mc.setUid(UUID.randomUUID().toString());
        mc.setCallsign(String.valueOf(CallString2)+" "+numberOfIteration2);
        numberOfIteration2=numberOfIteration2+1;
        mc.setType(String.valueOf(TypeString2));
        mc.showCotDetails(false);
        mc.setNeverPersist(true);
        Marker m = mc.placePoint();
        TextView Callsign = myFirstFragment.findViewById(R.id.textView2);
        TextView Type = myFirstFragment.findViewById(R.id.textView1);
        Callsign.setText(CallString2+" "+numberOfIteration2);
        Type.setText(TypeString2);
        // the stle of the marker is by default set to show an arrow, this will allow for full
        // rotation.   You need to enable the heading mask as well as the noarrow mask
        m.setStyle(m.getStyle()
                | Marker.STYLE_ROTATE_HEADING_MASK
                | Marker.STYLE_ROTATE_HEADING_NOARROW_MASK);
        m.setTrack(310, 20);
        m.setMetaInteger("color", Color.RED);
        m.setMetaString(UserIcon.IconsetPath, "34ae1613-9645-4222-a9d2-e5f243dea2865/Military/A10.png");
        m.refresh(getMapView().getMapEventDispatcher(), null,
                this.getClass());

    }


}
