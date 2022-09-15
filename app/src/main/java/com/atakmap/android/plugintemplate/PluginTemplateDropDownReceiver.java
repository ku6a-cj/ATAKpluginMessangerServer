
package com.atakmap.android.plugintemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.icons.UserIcon;
import com.atakmap.android.maps.Marker;
import com.atakmap.android.plugintemplate.plugin.DataModel;
import com.atakmap.android.plugintemplate.plugin.MyAPI;
import com.atakmap.android.user.PlacePointTool;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.plugintemplate.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PluginTemplateDropDownReceiver extends DropDownReceiver implements
        OnStateListener {

    public static final int SERVER_PORT = 3003;
    private ServerSocket serverSocket;
    private Socket tempClientSocket;
    private Thread serverThread;
    private LinearLayout msgList;
    private Handler handler;
    private EditText edMessage;
    // Get this IP from the Device WiFi Settings
    // Make sure you have the devices in same WiFi if testing locally
    // Or Make sure the port specified is open for connections.
    public static final String SERVER_IP = "192.168.0.52";
    private PluginTemplateDropDownReceiver clientThread;
    private Thread thread;
    private int clientTextColor;
    public static final String TAG = PluginTemplateDropDownReceiver.class
            .getSimpleName();
    public static final String SHOW_PLUGIN = "com.atakmap.android.plugintemplate.SHOW_PLUGIN";
    private final View templateView;
    private final Context pluginContext;
    private final View myFirstFragment;
    private final View helloView = null;
    private TextView txt;
    private int enemyCounter = 0;
    private  int allyCounter = 0;

    /**************************** CONSTRUCTOR *****************************/

    public PluginTemplateDropDownReceiver(final MapView mapView,
                                          final Context context) {
        super(mapView);
        this.pluginContext = context;
        handler = new Handler();


        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        templateView = PluginLayoutInflater.inflate(context,
                R.layout.main_layout, null);
        myFirstFragment = PluginLayoutInflater.inflate(pluginContext, R.layout.fragment_plugin_main, null);

        msgList = myFirstFragment.findViewById(R.id.msgList);
        edMessage = myFirstFragment.findViewById(R.id.edMessage);
        final String[] selectLista = new String[1];

        Button start_server = myFirstFragment.findViewById(R.id.start_server);
        Button send_data = myFirstFragment.findViewById(R.id.send_data);



        start_server.setOnClickListener(new View.OnClickListener() {
            private Thread serverThread;

            @Override
            public void onClick(View view) {
                //removeAllViews();
                showMessage("Server Started.", Color.BLACK);
                this.serverThread = new Thread(new ServerThread());
                this.serverThread.start();
            }
        });

        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edMessage.getText().toString().trim();
                showMessage("Server : " + msg, Color.BLUE);
                sendMessage(msg);
            }
        });

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

        String WIFi = getWifiIp();
        ///Log.e("My IP adress=",WIFi);
        showMessage("My IP adress="  + WIFi, Color.RED);


        final Button MyPositionButton = myFirstFragment.findViewById(R.id.button4);
        TextView GeoLoc = myFirstFragment.findViewById(R.id.textView3);

        final String[] ns = {"S"};
        final String[] we = {"E"};

        MyPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int LAT = (int) mapView.getSelfMarker().getPoint().getLatitude();
                int LONG = (int) mapView.getSelfMarker().getPoint().getLongitude();
                if(LAT>0){
                    ns[0] = "N";
                }else{
                    ns[0] ="S";
                    LAT =LAT * -1;
                }

                if(LONG>0){
                    we[0] = "E";
                }else{
                    we[0] ="W";
                    LONG = LONG * -1;
                }
                GeoLoc.setText(" Dane znacznika: Szerokosc:"+ LAT+ ns[0] +"  "+ "Dlugosc:"+ LONG + we[0] );
            }
        });

        Button getApiData = myFirstFragment.findViewById(R.id.getApiData);

        getApiData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApi();
            }
        });


    }

    private void callApi(){
        txt = myFirstFragment.findViewById(R.id.text_view_result);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://run.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instance for interface
        MyAPI MyAPICall = retrofit.create(MyAPI.class);

        Call<DataModel> call = MyAPICall.getData();

        call.enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                // that will be called on response
                if (response.code()!=200){
                    txt.setText("Check out the connection");
                    return;
                }
                String jsony="";

                jsony = "ID: "+ response.body().getid()+
                        "\nTitle: "+ response.body().getTitle()+
                        "\nLat: "+ response.body().getlat()+
                        "\nLon: "+ response.body().getlon();
                txt.append(jsony);
                if (response.body().getTitle().equals("Flag")){
                    CotEvent cotEvent = createPoint(response.body().getlat(), response.body().getlon());
                    cotEvent.setUID("ally"+ allyCounter);
                    allyCounter=+1;
                    CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
                }

            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
                //on failure
            }


        });
    }

    private CotEvent createPoint(double lat, double lon){


        CotPoint cotPoint = new CotPoint(lat, lon, 0.0, 2.0, 2.0);
        CotEvent cotEvent = new CotEvent();
        CoordinatedTime time= new CoordinatedTime();

        cotEvent.setTime(time);
        cotEvent.setStart(time);
        cotEvent.setHow("h-e");
        cotEvent.setType("a-f-G-U-C-I");
        cotEvent.setStale(time.addMinutes(10));
        cotEvent.setPoint(cotPoint);
        return cotEvent;
    }

    private String getWifiIp() {
        final WifiManager mWifiManager = (WifiManager) pluginContext.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                    + ((ip >> 24) & 0xFF);
        }
        return null;
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket;
            try {
                hideStartServerBtn();
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Error Starting Server : " + e.getMessage(), Color.RED);
            }
            if (null != serverSocket) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage("Error Communicating to Client :" + e.getMessage(), Color.RED);
                    }
                }
            }
        }
    }


    public TextView textView(String message, int color) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        String m = message + " [" + getTime() +"]";
        TextView tv = new TextView(pluginContext);
        tv.setTextColor(color);
        tv.setText(m);
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
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
    String TypeString2= "Strike Aircraft";
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
    public void showMessage(final String message, final int color) {
        handler.post(() -> msgList.addView(textView(message, color)));
    }

    private void removeAllViews(){
        handler.post(() -> msgList.removeAllViews());
    }

    private void hideStartServerBtn(){
        handler.post(() -> myFirstFragment.findViewById(R.id.start_server).setVisibility(View.GONE));
    }

    String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }


    private void sendMessage(final String message) {
        try {
            if (null != tempClientSocket) {
                new Thread(() -> {
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(tempClientSocket.getOutputStream())),
                                true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.println(message);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CommunicationThread implements Runnable {

        private final Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            tempClientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Error Connecting to Client!!", Color.RED);
                return;
            }
            showMessage("Connected to Client!!", Color.DKGRAY);
            sendMessage("Connected to Server!!");
        }

        public void PopUp(String read)
        {
            Log.e("moja wiadomosc", read);
            if (read.equals("1-DSM-0-0-0")){
                createAircraftWithRotation();
            }else if (read.equals("1-DSS-0-0-0")){
                createAtackAircraftWithRotation();
            }

           int i=0;
            read="ClientMessage-" + read;
            String[] words = read.split("-");
            for (String word : words) {
                Log.e("tabliczka", words[i]);
                i++;
            }


            if(i>4){
                if (words[2].equals("Flag") && words[1].equals("1")){
                    if(words[5].equals("1")){
                        words[3] = "-"+words[3];
                    }
                    if(words[6].equals("1")){
                        words[4] = "-"+words[4];
                    }
                    CotEvent cotEvent = createPoint(Double.parseDouble(words[3]), Double.parseDouble(words[4]));
                    cotEvent.setUID("ally"+String.valueOf(allyCounter));
                    allyCounter=allyCounter+1;
                    cotEvent.setType("a-f-G-U-C-I");
                    CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
                }

                if (words[2].equals("EFlag") && words[1].equals("1")){
                    if(words[5].equals("1")){
                        words[3] = "-"+words[3];
                    }
                    if(words[6].equals("1")){
                        words[4] = "-"+words[4];
                    }
                    CotEvent cotEvent = createPoint(Double.parseDouble(words[3]), Double.parseDouble(words[4]));
                    Log.e("enemyCounter1", String.valueOf(enemyCounter));
                    cotEvent.setUID("enemy"+String.valueOf(enemyCounter));
                    enemyCounter=enemyCounter+1;
                    Log.e("enemyCounter", String.valueOf(enemyCounter));
                    cotEvent.setType("a-h-G-U-C-I");
                    CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
                }

                if (words[2].equals("EFlag") && words[1].equals("2")){
                    if(words[5].equals("1")){
                        words[3] = "-"+words[3];
                    }
                    if(words[6].equals("1")){
                        words[4] = "-"+words[4];
                    }
                    CotEvent cotEvent = createPoint(Double.parseDouble(words[3]), Double.parseDouble(words[4]));
                    cotEvent.setUID("enemy"+String.valueOf(words[7]));
                    cotEvent.setType("a-h-G-U-C-I");
                    CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
                }

                if (words[2].equals("Flag") && words[1].equals("2")){
                    if(words[5].equals("1")){
                        words[3] = "-"+words[3];
                    }
                    if(words[6].equals("1")){
                        words[4] = "-"+words[4];
                    }
                    CotEvent cotEvent = createPoint(Double.parseDouble(words[3]), Double.parseDouble(words[4]));
                    cotEvent.setUID("ally"+String.valueOf(words[7]));
                    cotEvent.setType("a-f-G-U-C-I");
                    CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
                }



            }





        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) try {
                String read = input.readLine();
                PopUp(read);
                if (null == read || "Disconnect".contentEquals(read)) {
                    boolean interrupted = Thread.interrupted();
                    read = "Client Disconnected: " + interrupted;
                    showMessage("Client : " + read, Color.GREEN);
                    break;
                }
                showMessage("Client : " + read, Color.GREEN);
                Log.e("jestem tu4", "do konca");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
