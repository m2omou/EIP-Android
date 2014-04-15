package com.epitech.neerbyy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private List<Marker> listAllPlace;
	public Location locat = null;
	private Location centerScreen = null;
	public Place places;
	public ResponseWS rep;
	
	public int limit = 10;    //  def 10
	public int radius = 800;    //  def 800
	
	OnCameraChangeListener cc;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        
        if (!initCheckMap())
        {
        	Toast.makeText(this, "Error loading map", Toast.LENGTH_LONG).show();	
        	return;
        }
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
        		if (dist > radius)
        		{	
        			centerScreen.setLatitude(newPos.target.latitude);
        			centerScreen.setLongitude(newPos.target.longitude);
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
				//intent.put
	    		//b.putSerializable("placeInfo", pi);
				b.putString("placeId", pi.id);
				b.putString("placeName", pi.name);
	    		intent.putExtras(b);					
				startActivity(intent);
				return;
			}
		});
        
        gMap.setOnCameraChangeListener(cc);    
        //posMarker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(new LatLng(45.75, -0.633333)));
        listAllPlace = new ArrayList<Marker>();
        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        
        limit = 20;
        radius = 1000;
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
       // return new Double(dist * meterConversion).floatValue();
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
 
        //Si le GPS est disponible, on s'y abonne
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
        	abonnementWIFI();
        }
        /*if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            abonnementGPS();
        }
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            abonnement3G();
        }*/
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        //desabonnementGPS();
        desabonnementWIFI();
        //desabonnement3G();
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
        //On affiche dans un Toat la nouvelle Localisation
    	if (location != null)
    	{
    		final StringBuilder msg = new StringBuilder("lat : ");
    		msg.append(location.getLatitude());
    		msg.append( "; lng : ");
    		msg.append(location.getLongitude());
    		Log.w("GEOLOC", msg.toString());
    		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
          //  posMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            locat = location;
            if (centerScreen == null)
            	centerScreen = locat;
           
            if (locat == null)
            	new ThreadPlaces(this, null).start();
            else
            	new ThreadPlaces(this, new LatLng(locat.getLatitude(), locat.getLongitude())).start();
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
    	/*if("gps".equals(provider)) {
            abonnementGPS();
        }*/
    }
 
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) { }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
    
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
    			    		Toast.makeText(getApplicationContext(), "Get places error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	}
    			    	else if (Error == 3)
    			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	else
    			    	{
    			    		//places = (Place)pack.getSerializable("places");  //  UTILITEE ???
    			    		Toast.makeText(getApplicationContext(), "Places updated", Toast.LENGTH_SHORT).show();
    			    		msg.obj = places;
    			    		drawPlaces();
    			    	}		    	
    	    	} 	
    	    }
    	};
    	
    	public void drawPlaces()
    	{ 
    		for (int i = 0; i < places.list.length; i++)
    		{	
    			//Bitmap icon;
				/*try {
					icon = BitmapFactory.decodeStream(new URL(places.list[i].icon).openConnection().getInputStream());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
    			
    			//icon = Network.DownloadImage(places.list[i].icon); 
    			
    			
    			//check something mull  !!!!!!!!!!!!!!!!!
    			//listAllPlace.add(gMap.addMarker(new MarkerOptions().title(places.list[i].name).position(new LatLng(places.list[i].lat, places.list[i].lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.myPin)).snippet(places.list[i].address)));
    			if (places.list[i].marker == null) {
    				places.list[i].marker = gMap.addMarker(new MarkerOptions().title(places.list[i].name).position(new LatLng(places.list[i].lat, places.list[i].lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.myPin)).snippet(places.list[i].address));
    				listAllPlace.add(places.list[i].marker);
    			}
    		}
    	}
    	
    	private PlaceInfo getPlaceFromMarker(String id) {
    		
    		for (int i = 0; i < places.list.length; i++) {
    			Log.w("TEST", "ID M= " + id + " ET JAI " + places.list[i].marker.getId());
    			if (places.list[i].marker.getId().contains(id))
    				return places.list[i];
    		}
    		return null;
    	}
    	
    	private boolean initCheckMap() {   //  true ok
    	    if (gMap == null) {
    	    	gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    	        if (gMap != null)
    	            return true;
    	        return false;
    	    }
    	    return true;
    	}  	
}

