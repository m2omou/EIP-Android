package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.List;
 
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.epitech.neerbyy.Place.PlaceInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * This class represent the view of the Google Map view.
 * (see the GooglePlay api V2 doc)
 *  
 * @author Seb
 *
 */
public class MapView extends FragmentActivity implements LocationListener{
	
	private LocationManager locationManager;
	private GoogleMap gMap;
	private Marker posMarker;
	private List<Marker> listAllMarker;
	private List<Place.PlaceInfo> listAllPlaceInfos;
	public Location locat = null;
	private Location centerScreen = null;
	public Place places;
	public Place searchPlaces;
	public ResponseWS rep;
	
	public int limit = 10;    //  def 10
	public int radius = 800;    //  def 800
	public int bufferPlace = 20;   //  def  20
	OnCameraChangeListener cc;
	
	MarkerOptions markerOptions;
	LatLng latLng;
	
	EditText etLocation;
	    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        
        if (!initCheckMap())
        {
        	Toast.makeText(this, "Error loading map", Toast.LENGTH_LONG).show();	
        	return;
        }
       
        etLocation = (EditText) findViewById(R.id.et_location);
        
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        gMap.setOnCameraChangeListener(cc);
        
        cc = new OnCameraChangeListener(){
        	@Override
        	public void onCameraChange(CameraPosition newPos)
        	{
        		if (centerScreen == null)
        			return;
        		double dist = getDistance(new LatLng(centerScreen.getLatitude(),  centerScreen.getLongitude()), newPos.target);
        		Log.w("DISTANCE", Double.toString(dist));
        		if (dist > radius || listAllPlaceInfos.isEmpty())
        		{	
        			centerScreen.setLatitude(newPos.target.latitude);
        			centerScreen.setLongitude(newPos.target.longitude);
        			
        			//listAllPlace.clear();
               	 	Log.w("LANCE", "t3");
        			new ThreadPlaces(MapView.this, newPos.target).start();
        		}
        	}	
        };
        
        gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker m) {
				Intent intent = new Intent(MapView.this, ViewPost.class);
				Bundle b = new Bundle();
				PlaceInfo pi = getPlaceFromMarker(m.getId());
				
				if (pi == null)
				{
		    		Toast.makeText(getApplicationContext(), "Error find postInfo", Toast.LENGTH_SHORT).show();
					return;
				}
	    		
				//b.putSerializable("placeInfo", pi);
				int idFallowed = 0;
				if (pi.followed_place_id != 0)
					idFallowed = pi.followed_place_id;
				b.putString("placeId", pi.id);
				b.putString("placeName", pi.name);
				b.putDouble("latitude", pi.lat);
				b.putDouble("longitude", pi.lon);
				b.putInt("isFallowed", idFallowed);
	    		intent.putExtras(b);					
				startActivity(intent);
				return;
			}
		});
        
        gMap.setOnCameraChangeListener(cc);    
        //posMarker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(new LatLng(45.75, -0.633333)));
        listAllMarker = new ArrayList<Marker>();
        listAllPlaceInfos = new ArrayList<Place.PlaceInfo>();
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        limit = 20;
        radius = 1000;
        
        Button btn_find = (Button) findViewById(R.id.btn_find);      
        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Thread thread1 = new Thread(){
        	        public void run(){	        	      
        			try {	
                    	Gson gson = new Gson();
                    	String url = Network.URL + Network.PORT + "/search/places.json?query=" + etLocation.getText().toString();
                    	
                    	Message myMessage, msgPb;
                    	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
                    	myHandler.sendMessage(msgPb);
                    
                    	Bundle messageBundle = new Bundle();
            			messageBundle.putInt("action", ACTION.GET_SEARCH_PLACE.getValue());
            	        myMessage = myHandler.obtainMessage();	
                   
            	        InputStream input = Network.retrieveStream(url, METHOD.GET, null);
                    	
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
            						searchPlaces = rep.getValue(Place.class);         						
            					}
            					catch(JsonParseException e)
            				    {
            				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
            				    }
            					if (searchPlaces == null)
            					{
            						messageBundle.putInt("error", 2);
            						messageBundle.putString("msgError", rep.responseMessage);
            					}
            					else
            						Log.w("RECUP", "JAI RECUP DES PLACES SEARCH ");
            				}
            			}     
            	        myMessage.setData(messageBundle);
                        myHandler.sendMessage(myMessage);
                        
                        msgPb = myHandler.obtainMessage(1, (Object) "Success");
                        myHandler.sendMessage(msgPb);                       
                	}
                	catch (Exception e) {
                        e.printStackTrace();}
                	Log.w("THREAD", "FIN THREAD SEARCH PLACES");      			
        	    }};
        	    thread1.start();
            }
        };     
        btn_find.setOnClickListener(findClickListener);   
    }
    
    public double getDistance(LatLng oldPos, LatLng newPos) {
    	double lat1 = oldPos.latitude;
    	double lng1 = oldPos.longitude;
    	double lat2 = newPos.latitude;
    	double lng2 = newPos.longitude;
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return Double.valueOf(dist * meterConversion);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
        
    	if (!initCheckMap())
        {
        	Toast.makeText(this, "Error loading map", Toast.LENGTH_LONG).show();	
        	return;
        }
    	
        //Obtention de la référence du service
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
 
        //Si le wifi est disponible, on s'y abonne
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
        	abonnementWIFI();
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            abonnementGPS();
        }
        /*if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            abonnement3G();
        }*/
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        desabonnementGPS();
        desabonnementWIFI();
        //desabonnement3G();
    }
    
    /**
     * Méthode permettant de s'abonner à la localisation par GPS.
     */
    public void abonnementGPS() {
    	 Log.w("MAP", "iciiiiiiiiiiii gps");
        //On s'abonne
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }
    
    public void abonnementWIFI() {
        //On s'abonne
    	Log.w("MAP", "iciiiiiiiiiiiiwifi");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
    }
    
    public void abonnement3G() {
        //On s'abonne
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 10, this);
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
    
    public void desabonnement3G() {
        //Si le GPS est disponible, on s'y abonne
        locationManager.removeUpdates(this);
    }
    
    @Override
    public void onLocationChanged(final Location location) {
    	Log.w("LOCATION_CHANGE", "iciiiiiiiiiiii");
    	if (location != null)
    	{ 		
    		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            //posMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    		//gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.858093f, 2.294694f), 15));
	 
    		locat = location;
            if (centerScreen == null)
            	centerScreen = locat;
           
            if (locat == null)
            {	
           	 	Log.w("LANCE", "t1");
            	new ThreadPlaces(this, null).start();
            }
            else
            {
           	 	//Log.w("LANCE", "t2");
            	//new ThreadPlaces(this, new LatLng(locat.getLatitude(), locat.getLongitude())).start();
            }
        }
    }
 
    @Override
    public void onProviderDisabled(final String provider) {
        //Si le GPS est désactivé on se désabonne
    	 Log.w("Map", "iciiiiiiiiiiii8");
        if("gps".equals(provider))
            desabonnementGPS();
        if("NETWORK_PROVIDER".equals(provider))
            desabonnementWIFI();
    }
 
    @Override
    public void onProviderEnabled(final String provider) {
    	Log.d("Map", "iciiiiiiiiiiii9");
    	if("NETWORK_PROVIDER".equals(provider)){
        	abonnementWIFI();
        }
    	/*if("gps".equals(provider)) {
            abonnementGPS();
        }*/
    }
 
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {}
    
     Handler myHandler = new Handler()
    	{
    	    @Override 
    	    public void handleMessage(Message msg)
    	    {
    	    	Bundle pack = msg.getData();
    	    	int Error = pack.getInt("error");
    	    	
    	    	switch (Network.ACTION.values()[pack.getInt("action")])
    	    	{
    		    	case GET_PLACES:    				    			    	
    			    	if (Error == 1)
    			    		Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_LONG).show();
    			    	else if (Error == 2)
    			    	{
    			    		Toast.makeText(getApplicationContext(), "Get places error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	}
    			    	else if (Error == 3)
    			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	else
    			    	{
    			    		Toast.makeText(getApplicationContext(), "Places updated", Toast.LENGTH_SHORT).show();
    			    		msg.obj = places;
    			    		drawPlaces();
    			    	}	
    			    break;
    			    
    		    	case GET_SEARCH_PLACE:
    		    		if (Error == 1)
    			    		Toast.makeText(MapView.this, "Error: connection with WS fail", Toast.LENGTH_LONG).show();
    			    	else if (Error == 2)
    			    	{
    			    		Toast.makeText(getApplicationContext(), "Get search places error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	}
    			    	else if (Error == 3)
    			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	else
    			    	{
    			    		Toast.makeText(getApplicationContext(), "Search Places find : " + searchPlaces.list.length, Toast.LENGTH_SHORT).show();
    			    		
    			    		if (searchPlaces.list.length == 0)
    			    			return;
    			    		
    			    		List<CharSequence> charSequences = new ArrayList<CharSequence>();
    			    		for (int i = 0; i < searchPlaces.list.length; i++) {
    			    			String tmp = new String(searchPlaces.list[i].name + ":\n");
    			    			if (searchPlaces.list[i].address != null)
    			    				tmp += searchPlaces.list[i].address;
    			    			if (searchPlaces.list[i].country != null)
    			    				tmp += ", " + searchPlaces.list[i].country;
    			    			if (searchPlaces.list[i].city != null)
    			    				tmp += ", " + searchPlaces.list[i].city;
    			    			
    			    			charSequences.add(tmp);
    			    		}
    			    		final CharSequence[] charSequenceArray = charSequences.toArray(new
    			    			    CharSequence[charSequences.size()]);
    			    		
							AlertDialog.Builder builder = new AlertDialog.Builder(MapView.this);
							builder.setTitle("Que Cherchez vous ?");
							builder.setItems(charSequenceArray, new DialogInterface.OnClickListener() {
							          
									public void onClick(DialogInterface dialog, int item) {
							                Toast.makeText(getApplicationContext(), charSequenceArray[item], Toast.LENGTH_SHORT).show();
							               
							                CameraPosition cp = new CameraPosition(new LatLng(searchPlaces.list[item].lat, searchPlaces.list[item].lon), 15, 0, 0);
							                etLocation.setText("");						                
							                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cp.target, 15));						                
							               // new ThreadPlaces(MapView.this, cp.target).start();
							          	}
							        });
							AlertDialog alert = builder.create();
							alert.show();			
    			    }	
    	    	} 	
    	    }
    	};
    	
    	public void drawPlaces()
    	{ 
    		for (int i = 0; i < places.list.length; i++)
    		{	
    			if (!isAlreadyHere(places.list[i]))
    			{
    				places.list[i].marker = gMap.addMarker(new MarkerOptions().title(places.list[i].name).position(new LatLng(places.list[i].lat, places.list[i].lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.myPin2)).snippet(places.list[i].address));	
    				listAllPlaceInfos.add(places.list[i]);
    				listAllMarker.add(places.list[i].marker);		
    			}		
    		}		
    	Log.d("DELM", "DEB VEC " + listAllMarker.size() + "/" + bufferPlace);
		if (listAllMarker.size() > bufferPlace) {
			while (listAllMarker.size() > bufferPlace) {
				//listAllMarker.get(listAllMarker.size() - 1).remove();
				//listAllMarker.remove(listAllMarker.size() - 1);  // dernier ou first ?
				listAllMarker.get(0).remove();
				listAllMarker.remove(0);
			}
			Log.d("DELM", "FIN VEC " + listAllMarker.size() + "/" + bufferPlace);
    	}
    }
    	
    	private boolean isAlreadyHere(Place.PlaceInfo pi) {
    		//Marker mpi = pi.marker;
    		
    		for (Place.PlaceInfo list : listAllPlaceInfos) {
    			if (pi.name.contains(list.name))
    				return true;
    		}
    		   		
    		/*for (Marker m : listAllMarker) {
    			if (pi.name.contains(m.getTitle()))
    				return true;
    		}
    		*/
    		return false;
    	}
    	
    	private PlaceInfo getPlaceFromMarker(String id) {
    		
    		for (Place.PlaceInfo pi : listAllPlaceInfos) {
    			Log.d("TEST", "ID M= " + id + " ET JAI " + pi.marker.getId());
    			if (pi.marker.getId().contains(id))
    				return pi;
    		}
    		return null;
    	}
    	
    	private boolean initCheckMap() {
    	    if (gMap == null) {
    	    	gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    	        if (gMap != null)
    	            return true;
    	        return false;
    	    }
    	    return true;
    	}
    	
    	@Override
    	public boolean onCreateOptionsMenu(Menu menu) {
    		getMenuInflater().inflate(R.layout.menu, menu);
    		menu.getItem(2).getSubMenu().setHeaderIcon(R.drawable.ic_launcher);
    		return super.onCreateOptionsMenu(menu);
    	}
    	
    	@Override
    	
    	public boolean onOptionsItemSelected(MenuItem item) {
    		Intent intent;
            switch (item.getItemId()) {
               case R.id.LocateMe:
                  //intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.fr"));
                  intent = new Intent(this, Geoloc.class);
                  startActivity(intent);
                  return true;
               case R.id.getUser:
                   intent = new Intent(this, EditInfoUser.class);
    			   startActivity(intent);
                   return true;
               case R.id.User:
                   return true;
               case R.id.Login:
            	   intent = new Intent(this, Login.class);
    			   startActivity(intent);
            	   return true;
               case R.id.CreateAccount:
            	   intent = new Intent(this, CreateAccount.class);
    			   startActivity(intent);
            	   return true;
               case R.id.Feed:
            	   intent = new Intent(this, ViewFeed.class);
    			   startActivity(intent);
            	   return true;
               case R.id.Conv:
            	   intent = new Intent(this, ViewConv.class);
    			   startActivity(intent);
            	   return true;
               case R.id.Menu:
            	   if (Network.USER == null)
            		   intent = new Intent(this, Login.class);
            	   else
            		   intent = new Intent(this, Menu2.class);
    			   startActivity(intent);
            	   return true;
               case R.id.quit:
                  finish();
                  return true;
               case R.id.testToken:
            	   intent = new Intent(this, GetUserById.class);
    			   startActivity(intent);
    			   break;   //  ou return true ??
               case R.id.mapView:
            	   intent = new Intent(this, MapView.class);
    			   startActivity(intent);
    			   break;
            }
            return super.onOptionsItemSelected(item);
        }

}

