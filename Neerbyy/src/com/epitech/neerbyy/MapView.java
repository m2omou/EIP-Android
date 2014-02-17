package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MapView extends FragmentActivity implements LocationListener{
	
	private LatLng pos;
	private LocationManager locationManager;
	private GoogleMap gMap;
	private Marker marker;
	private Location locat = null;
	
	private Place[] places;
	
	private ResponseWS rep;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        
        
        gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        marker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(new LatLng(45.75, -0.633333)));
       
        //mMap.setMyLocationEnabled(true);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
        
        //Obtention de la référence du service
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
 
        //Si le GPS est disponible, on s'y abonne
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
        	abonnementWIFI();
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            abonnementGPS();
        }
        
       // onLocationChanged(null);  //  a   enleverrrrrr
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        //On appelle la méthode pour se désabonner
        desabonnementGPS();
        desabonnementWIFI();
    }
    
    /**
     * Méthode permettant de s'abonner à la localisation par GPS.
     */
    public void abonnementGPS() {
        //On s'abonne
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }
    
    public void abonnementWIFI() {
        //On s'abonne
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
    }
 
    /**
     * Méthode permettant de se désabonner de la localisation par GPS.
     */
    public void desabonnementGPS() {
        //Si le GPS est disponible, on s'y abonne
        locationManager.removeUpdates(this);
    }
    
    public void desabonnementWIFI() {
        //Si le GPS est disponible, on s'y abonne
        locationManager.removeUpdates(this);
    }
    
    @Override
    public void onLocationChanged(final Location location) {
        //On affiche dans un Toat la nouvelle Localisation
    	if (location != null)
    	{
    		final StringBuilder msg = new StringBuilder("lat : ");
    		msg.append(location.getLatitude());
    		msg.append( "; lng : ");
    		msg.append(location.getLongitude());
    		Log.w("GEOLOC", msg.toString());
    		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            locat = location;
            if (!tGetPlace.isAlive())
            	tGetPlace.start();
    	}
    }
 
    @Override
    public void onProviderDisabled(final String provider) {
        //Si le GPS est désactivé on se désabonne
        if("gps".equals(provider))
            desabonnementGPS();
        if("NETWORK_PROVIDER".equals(provider))
            desabonnementWIFI();
    }
 
    @Override
    public void onProviderEnabled(final String provider) {
        
    	if("NETWORK_PROVIDER".equals(provider)){
        	abonnementWIFI();
        }
    	if("gps".equals(provider)) {
            abonnementGPS();
        }
    }
 
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) { }
    
    

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
      
    
    private Thread tGetPlace = new Thread(){
        public void run(){
        	try {
	        	Gson gson = new Gson();
	        	String url;
	        	if (locat != null){
	        		url = Network.URL + Network.PORT + "/places.json?latitude=" + locat.getLatitude() + "&longitude=" + locat.getLongitude();
	        	}
	        	else {        		
	        		url = Network.URL + Network.PORT + "/places.json?latitude=45.75&longitude=-0.633333";
	        	}
	        	Message myMessage;
	        	Bundle messageBundle = new Bundle();
				messageBundle.putInt("action", Network.GET_PLACES);
		        myMessage = myHandler.obtainMessage();	
	       
		        InputStream input = Network.retrieveStream(url, 0, null);
	        	
		        if (input == null)
					messageBundle.putInt("error", 1);
				else
				{	
					Reader readerResp = new InputStreamReader(input);
					String ret = Network.checkInputStream(readerResp);
					
					if (ret.charAt(0) != '{' && ret.charAt(0) != '[')
					{
						messageBundle.putInt("error", 3);
						messageBundle.putString("msgError", ret);
					}
					else
					{
						try {		    
							rep = gson.fromJson(ret, ResponseWS.class);
							places = rep.getTabValue(Place.class);
							Log.w("RECUP", "JAI RECUP " + places.length + " places");
						}
						catch(JsonParseException e)
					    {
					        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
					    }
						if (places == null)
						{
							messageBundle.putInt("error", 2);
							messageBundle.putString("msgError", rep.responseMessage);
						}
						else		  	                   
							messageBundle.putSerializable("places", (Serializable) places);
					}
				}
		        myMessage.setData(messageBundle);
                myHandler.sendMessage(myMessage);
        	}
        	catch (Exception e) {
                e.printStackTrace();}
        }};
    
    
        Handler myHandler = new Handler()
    	{
    	    @Override 
    	    public void handleMessage(Message msg)
    	    {
    	    	Bundle pack = msg.getData();
    	    	switch (pack.getInt("action"))
    	    	{
    		    	case Network.GET_PLACES:    		
    		    		
    			    	int Error = pack.getInt("error");		    	
    			    	if (Error == 1)
    			    		Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_LONG).show();
    			    	else if (Error == 2)
    			    	{
    			    		Toast.makeText(getApplicationContext(), "Login error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	}
    			    	else if (Error == 3)
    			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	else
    			    	{
    			    		places = (Place[])pack.getSerializable("places");  //  UTILITEE ???
    			    		Toast.makeText(getApplicationContext(), "Places updated", Toast.LENGTH_SHORT).show();
    			    		msg.obj = places;    		
    			    	}
    	    	} 	
    	    }
    	};
}


/*import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;

public class MapView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }
}
*/
