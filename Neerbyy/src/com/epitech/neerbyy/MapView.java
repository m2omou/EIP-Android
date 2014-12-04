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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.epitech.neerbyy.Place.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
	
	//----------------------------------------NEW----------------------------------------
	private Context mContext;
	//protected PlaceActivity activity;
	private FollowMeLocationSource followMeLocationSource;
	//-----------------------------------------------------------------------------------   
	
	private LocationManager locationManager;
	private GoogleMap gMap;
	private Marker posMarker;
	
	private List<Marker> listAllMarker;
	private List<Marker> listGreenMarker;
	
	private List<Place.PlaceInfo> listAllPlaceInfos;
	public Location locat = null;
	//private Location centerScreen = null;
	public Place places;
	public Place searchPlaces;
	public ResponseWS rep;
	
	public Categorie categories;
	public String categorieId;
	
	public int limit = 10;    //  def 10
	public int radius = 800;    //  def 800
	public int bufferPlace = 20;   //  def  20
	OnCameraChangeListener cc;
	
	MarkerOptions markerOptions;
	LatLng latLng;
	
	//EditText etLocation;
	
	private MenuItem item_loading;
	private MenuItem item_filtre;
	OnMenuItemClickListener changeFilter;
	
	SearchView mSearchView;
	
	private void GetCurrentLocation() {
		double[] d = getlocation();
	    //gMap.addMarker(new MarkerOptions().position(new LatLng(d[0], d[1])).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_menu2)));
	    //gMap.addMarker(new MarkerOptions().position(new LatLng(d[0], d[1])).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	    latLng = new LatLng(d[0], d[1]);
	    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d[0], d[1]), 16));
	    new ThreadPlaces(MapView.this, latLng).start();  
	}
	
	public double[] getlocation() {
	    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    List<String> providers = lm.getProviders(true);

	    Location l = null;
	    for (int i = 0; i < providers.size(); i++) {
	        l = lm.getLastKnownLocation(providers.get(i));
	        if (l != null)
	            break;
	    }
	    double[] gps = new double[2];

	    if (l != null) {
	        gps[0] = l.getLatitude();
	        gps[1] = l.getLongitude();
	    }
	    return gps;
	}
	  
	private void setUpMapIfNeeded() {
	    if (gMap == null) {
	        Log.e("", "map is null");
	        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

	        if (gMap != null) {
	            Log.e("", "map is full");
	            gMap.setMapType(gMap.MAP_TYPE_NORMAL);
	            
	            // Replace the (default) location source of the my-location layer with our custom LocationSource
                //gMap.setLocationSource(followMeLocationSource);
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
                gMap.getUiSettings().setZoomControlsEnabled(false);
                gMap.setOnCameraChangeListener(cc);
                
                // Set default zoom
                //gMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
	            //gMap.getUiSettings().setZoomControlsEnabled(false);
	        }
	    }
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        
//-----------------------NEW-------------------------------------------
        mContext = getApplicationContext();
        //followMeLocationSource = new FollowMeLocationSource(); 
 
//-------------------------------------------------------------------------
        
        /*if (!initCheckMap())
        {
        	Toast.makeText(this, "Erreur lors du chargement de la carte", Toast.LENGTH_LONG).show();	
        	return;
        }*/
        
         
        
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		    
        cc = new OnCameraChangeListener(){
        	@Override
        	public void onCameraChange(CameraPosition newPos)
        	{
        		Log.w("DISTANCE", "CAMERA CHANGGGGGEEEEEE");
        		if (latLng == null) { 
        			Log.e("Error", "latLng null !!!!");
        			return;
        		}
        		
        		double dist = getDistance(new LatLng(latLng.latitude,  latLng.longitude), newPos.target);
        		Log.w("DISTANCE", Double.toString(dist));
        		if (dist > radius || listAllPlaceInfos.isEmpty())
		        	{	
        				latLng = new LatLng(newPos.target.latitude, newPos.target.longitude);
        				
        				gMap.clear();
			        	listAllPlaceInfos.clear();
			        	listAllMarker.clear();
			        	//listGreenMarker.clear();
			        					        			
			            Log.w("LANCE", "t3.1");
			        	new ThreadPlaces(MapView.this, newPos.target).start();
		        	}   				
        		}
        };
        
        setUpMapIfNeeded();
    ///////////////////////////CLICK  ON  LOCAT  BUTTON////////////////////////////////////  
        gMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

			@Override
		    public boolean onMyLocationButtonClick() {
				gMap.clear();
				listAllPlaceInfos.clear();
	        	listAllMarker.clear();
	        	//listGreenMarker.clear();
	        	
	        	GetCurrentLocation();
				//new ThreadPlaces(MapView.this, new LatLng(latLng.latitude, latLng.longitude)).start();
		        return false;
		    }
		});
	///////////////////////////////////////////////////////////////////////////////////////
        GetCurrentLocation();   
    
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
        
       
        //posMarker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(new LatLng(45.75, -0.633333)));
        listAllMarker = new ArrayList<Marker>();
        listGreenMarker = new ArrayList<Marker>();
        listAllPlaceInfos = new ArrayList<Place.PlaceInfo>();
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        limit = 70;
        radius = 200;     
          
    
        changeFilter = new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				if (categories == null)
				{
					Toast.makeText(getApplicationContext(), "Désolé, les catégories ne sont pas encore disponible :(", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				//List<String> list = new ArrayList<String>();
				final CharSequence[] items = new CharSequence[categories.list.length]; //  = {"Autoriser les autres utilisateurs à me contacter"}//;, "Autoriser commentaires sur mes publications", "Autoriser commentaires sur mes messages"};
				//final boolean[] check = new Boolean[categories.list.length];    //new ArrayList<Boolean>();      // = {false}
							
				for (int i = 0; i < categories.list.length; i++) {
					items[i] = categories.list[i].name;
					//check[i] = false;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MapView.this);
				builder.setTitle("Choisissez un filtre :");
				builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {     //(items, check, new DialogInterface.OnMultiChoiceClickListener() {
				          
				@Override
				public void onClick(DialogInterface dialog, int which) {
					categorieId = categories.list[which].id;
					
				}
				}).setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				}).setPositiveButton("Valider", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					
						//  jai remplace gMap.getLocation.getLon lat
						new ThreadPlaces(MapView.this, new LatLng(latLng.latitude, latLng.longitude)).start(); 
						
						//item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
						//item_loading.setVisible(true);
					}
				});
				
				AlertDialog alert = builder.create();
				alert.setCancelable(true);
				alert.show();
				return false;				
			}
        };     
        threadGetCate.start();
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

        // Get a reference to the map/GoogleMap object
        setUpMapIfNeeded();
        /* Enable the my-location layer (this causes our LocationSource to be automatically activated.)
         * While enabled, the my-location layer continuously draws an indication of a user's
         * current location and bearing, and displays UI controls that allow a user to interact
         * with their location (for example, to enable or disable camera tracking of their location and bearing).*/
        gMap.setMyLocationEnabled(true);
        getBestProvider();
    }
    
    public void getBestProvider() {
    	 //LocationManager locationManager2;
         Criteria criteria = new Criteria();
         String bestAvailableProvider;
         int minTime = 10000;     // minimum time interval between location updates, in milliseconds
         int minDistance = 10;    // minimum distance between location updates, in meters
         
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //Specify Location Provider criteria
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        //criteria.setAltitudeRequired(false);
        //criteria.setBearingRequired(false);
        //criteria.setSpeedRequired(false);
        //criteria.setCostAllowed(true);
   
        /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use 
         * is to ask the Location Manager for the one that best satisfies our criteria.
         * By passing the 'true' boolean we ask for the best available (enabled) provider. */
        bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        
    
        if (bestAvailableProvider != null) {
        	locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);     
        	//Location location = locationManager.getLastKnownLocation(bestAvailableProvider);
        	//gMap.get         
        } 
        else {
        	Log.w("Error", "No Location Providers currently available !!!!!");
        	// (Display a message/dialog) No Location Providers currently available.
        }
    	
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	//--------------------------------------MAP----------------------------------------------
    	  /* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */   
        //-------------------------------------------------------------------------------------
    	gMap.setMyLocationEnabled(false);
    	locationManager.removeUpdates(this);
    }
         
    @Override
    public void onLocationChanged(final Location location) {
    	Log.w("LOCATION_CHANGE", "iciiiiiiiiiiii");
    //	if (1 == 1)
    	//	return;
    	//Toast.makeText(getApplicationContext(), "ON LOCATION_CHANGE", Toast.LENGTH_SHORT).show();
    	
    	//	item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	//	item_loading.setVisible(true);
    	
    	if (location != null)
    	{ 		
    		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            //posMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    		//gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.858093f, 2.294694f), 15));
	 
    		locat = location;
          
            if (locat == null)
            {	
           	 	Log.w("LANCE", "t1");
            	new ThreadPlaces(this, null).start();
            }
            /*else
            {
           	 	Log.w("LANCE", "t2");
            	new ThreadPlaces(this, new LatLng(locat.getLatitude(), locat.getLongitude())).start();
            }*/
        }
    }
 
    @Override
    public void onProviderDisabled(final String provider) {
    	locationManager.removeUpdates(this);
    }
 
    @Override
    public void onProviderEnabled(final String provider) {
    	int minTime = 10000;     // minimum time interval between location updates, in milliseconds
        int minDistance = 10;    // minimum distance between location updates, in meters
    	
        Log.d("Map", "Provider Detecter= " + provider);
    	locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
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
    		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
    			    	else if (Error == 2)
    			    	{
    		    			Toast.makeText(getApplicationContext(), "Erreur lors de la recuperation des lieux: " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
    			    	}
    			    	else if (Error == 3)
    			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
    			    	else
    			    	{
    			    		//Toast.makeText(getApplicationContext(), "Places updated", Toast.LENGTH_SHORT).show();
    			    		
    			    		//gMap.clear();   do it ???????????
    			    		
    			    		drawPlaces();
    			    	}
    		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    		      		item_loading.setVisible(false);
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
    			    		//Toast.makeText(getApplicationContext(), "Search Places find : " + searchPlaces.list.length, Toast.LENGTH_SHORT).show();
    			    		
    			    		if (searchPlaces.list.length == 0)
    			    		{
        			    		Toast.makeText(getApplicationContext(), "Aucune place trouvé :(", Toast.LENGTH_SHORT).show();
    			    			return;
    			    		}
    			    		
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
    			    		
    			    		//Toast.makeText(getApplicationContext(),"Charsequence a " + charSequenceArray.length, Toast.LENGTH_SHORT).show();

    			    		
							AlertDialog.Builder builder = new AlertDialog.Builder(MapView.this);
							builder.setTitle("Que Cherchez vous ?");
							builder.setItems(charSequenceArray, new DialogInterface.OnClickListener() {
							          
								@Override
									public void onClick(DialogInterface dialog, int item) {
							                //Toast.makeText(getApplicationContext(), charSequenceArray[item], Toast.LENGTH_SHORT).show();
							               
							                CameraPosition cp = new CameraPosition(new LatLng(searchPlaces.list[item].lat, searchPlaces.list[item].lon), 15, 0, 0);
							                //etLocation.setText("");						                
							                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cp.target, 15));
							                locationManager.removeUpdates(MapView.this);   //   changer ici   new
							               // new ThreadPlaces(MapView.this, cp.target).start();
							          	}
							        });
							AlertDialog alert = builder.create();
							alert.show();			
    			    }
    		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    		      		item_loading.setVisible(true);
    		    	break;
    		    	
    		    	case UPDATE_ICON_MARKER:    				    			    	
    			    	if (Error == 1)
    			    		Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_LONG).show();
    			    	else if (Error == 2)
    			    	{
    			    		Toast.makeText(getApplicationContext(), "Update icon marker error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	}
    			    	else if (Error == 3)
    			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
    			    	else
    			    	{
    			    		//Toast.makeText(getApplicationContext(), "Icone marker updated", Toast.LENGTH_SHORT).show();
    			    		int indice = pack.getInt("indicePost");
    			    		
    			    		if (places.list.length > indice && places.list[indice].marker != null) {             //  Truc null parfois a decouvrir
    			    			/*if (places.list[indice].marker == null)
    			    				Log.w("MAP", "MAR");
    			    			if (places.list[indice].bitmap == null)
    			    				Log.w("MAP", "BIT");*/
    	//A  REMETTRE  			//places.list[indice].marker.setIcon(BitmapDescriptorFactory.fromBitmap(places.list[indice].bitmap));	    		
    			    		}
    			    	}
    			    	item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    		      		item_loading.setVisible(false);
    			    break;
    	    	}	    	
    	    }
    	};
    	
    	public void drawPlaces()
    	{ 
    		for (int i = 0; i < places.list.length; i++)
    		{	
    			if (!isAlreadyHere(places.list[i]))
    			{
    				//places.list[i].markerDef = gMap.addMarker(new MarkerOptions().position(new LatLng(places.list[i].lat, places.list[i].lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin1)));
    				places.list[i].marker = gMap.addMarker(new MarkerOptions().title(places.list[i].name).position(new LatLng(places.list[i].lat, places.list[i].lon)).snippet(places.list[i].address).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_dark_green)));
    				
    				//  A  REMETTRE
    				/*if (places.list[i].markerDef == null)
    					Log.w("MAP", "MARDEF null");
    				else 
    				{
    					//places.list[i].markerDef.hideInfoWindow();
    					//places.list[i].markerDef.setDraggable(false);
    					//places.list[i].markerDef.setFlat(true);
        				//places.list[i].markerDef.setInfoWindowAnchor(-1, -1);
        				//places.list[i].markerDef.setAnchor(-1, -1);
    					
    					//  A  REMETTRE listGreenMarker.add(places.list[i].markerDef);
    				}*/
    				
    				if (places.list[i].marker == null)
    					Log.w("MAP", "MARK null");
    				else 
    				{
    					listAllMarker.add(places.list[i].marker);
    				}
    				//  A  REMETTRE     new ThreadDownloadImage(MapView.this, i).start();
    				listAllPlaceInfos.add(places.list[i]);  				
    			}
    			
    		}		
    	
  ////////////////////////////////////////////////////////////////////////////EFFACE////////////////  		
    	Log.d("DELM", "DEB VEC " + listAllMarker.size() + "/" + bufferPlace);
		if (listAllMarker.size() > bufferPlace) {
			while (listAllMarker.size() > bufferPlace) {
				//listAllMarker.get(listAllMarker.size() - 1).remove();
				//listAllMarker.remove(listAllMarker.size() - 1);  // dernier ou first ?
				Marker m;
				
				m = listAllMarker.get(0);
				m.remove();
				listAllMarker.remove(0);
				
				//A  REMETTRE
				/*m = listGreenMarker.get(0);
				m.remove();		
				listGreenMarker.remove(0);*/
			}
			Log.d("DELM", "FIN VEC " + listAllMarker.size() + "/" + bufferPlace);
    	}
		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
  		item_loading.setVisible(true);
    }
   ///////////////////////////////////////////////////////////////////////////////////////////////////
    	
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
    		 try {
    	     
    	  /*  if (gMap == null) {
    	    	
    	    	//Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
    	    	//SupportMapFragment mapFragment = (SupportMapFragment) fragment;
    	    	//gMap = mapFragment.getMap();
    	    	
    	    	
    	    	 android.support.v4.app.FragmentManager myFragmentManager = getSupportFragmentManager();
    	    	 SupportMapFragment mySupportMapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
    	         gMap = mySupportMapFragment.getMap();
    	         if (gMap != null)
    	        	 return true;*/
    	    	

    	       
    	    	
    	    	/*if (gMap == null) {
        	    	gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        	        if (gMap != null)
        	            return true;     	    
        	    }
        	    return false;*/
    	    	
    	    	
    	    //	MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

    	    	//android.support.v4.app.FragmentManager myFragmentManager = getSupportFragmentManager();
    	    	
    	    	/*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    	    	if (mapFragment == null)
    	    	{
    	    		Log.w("MAP", "merde");    		 
    	    		mapFragment = SupportMapFragment.newInstance();
    	    	}
    	    	/*android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
    	    	android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	    	fragmentTransaction.add(R.id.map, mapFragment);  
    	    	fragmentTransaction.commit();
    	    	
    	    	gMap = mapFragment.getMap();
    	    	if (gMap != null)
    	    		return true;
    	    	Log.w("MAP", "merde2");*/
     	        
     	        
    	    	//SupportMapFragment mySupportMapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);

    	    	
    	    	/*FragmentManager fm = getFragmentManager();
    	        FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	        fragmentTransaction.add(R.id.map, mapFragment);  
    	        fragmentTransaction.commit();*/
    	        
    	        
    	        //gMap = mapFragment.getMap();
    	    	
    	    	//CustomMapFragment mapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    	    	
    	    	//CustomMapFragment mapFragment = (CustomMapFragment) CustomMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
    	        //mapFragment.setTargetFragment(((CustomMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)), 1);
    	    	
    	    	//gMap = mapFragment.getMap();
    	    	
    	    	//gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    	      //  if (gMap != null)
    	        //    return true;
    	         
    	         if (gMap == null) {
    	             gMap = ((MapFragment) getFragmentManager().findFragmentById(
    	                     R.id.map)).getMap();
    	  
    	             // check if map is created successfully or not
    	             if (gMap == null) {
    	                 Toast.makeText(getApplicationContext(), "Desole, impossible de cree la carte", Toast.LENGTH_SHORT).show();
    	                 return false;
    	             }   	            
    	         } 
    	         else
	            	 return true;
    		 } catch (Exception e) {
    			 e.printStackTrace();
    			 Log.e("ERROR", e.getMessage());
                 Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

    			 
 	    	}
    		return true;   //  nor false bug exeption when verif null
    	}
    	
    	@Override
  	    public boolean onCreateOptionsMenu(Menu menu) {
  	    MenuInflater inflater = getMenuInflater();
  	    inflater.inflate(R.menu.map_view, menu); 
  	    item_loading = menu.findItem(R.id.loading_zone);
  		item_loading.setVisible(false);
  		
  		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
  		item_loading.setVisible(true);
  		
  		item_filtre = menu.findItem(R.id.filtre);
  		item_filtre.setOnMenuItemClickListener(changeFilter);
		
  	// SearchView
  		MenuItem itemSearch = menu.findItem(R.id.search_add);
  		mSearchView = (SearchView) itemSearch.getActionView();
  		mSearchView.setQueryHint("Entrez une adresse");
  		mSearchView.setIconifiedByDefault(true);

  		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

  			@Override
  			public boolean onQueryTextSubmit(String query) {
  				Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
  				
  				mSearchView.setIconified(true);
  				new ThreadUpdateAdresse(MapView.this, query).start();
  				return true;
  			}

  			@Override
  			public boolean onQueryTextChange(String newText) {
  				return false;
  			}
  		});	
  	    return true;
  	  }
  		
  	 @Override
  	  public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
  	    case R.id.logo_menu:
  	    	item_loading = item;
  	    	Intent intent;
	    	if (Network.USER == null)
	    		intent = new Intent(MapView.this, Login.class);
	    	else
	    		intent = new Intent(MapView.this, Menu2.class);
  			startActivity(intent);
  			break;
  	    default:
  	    	break;
  	    }
  	    return true;
  	 }
  	 
  	 
  	 //-----------------------------------------------------------NEW-----------------------------
  	 
  	 
  	 /* Our custom LocationSource. 
      * We register this class to receive location updates from the Location Manager
      * and for that reason we need to also implement the LocationListener interface. */
     private class FollowMeLocationSource implements LocationSource, LocationListener {

         private OnLocationChangedListener mListener;
         private LocationManager locationManager;
         private final Criteria criteria = new Criteria();
         private String bestAvailableProvider;
         /* Updates are restricted to one every 10 seconds, and only when
          * movement of more than 10 meters has been detected.*/
         private final int minTime = 10000;     // minimum time interval between location updates, in milliseconds
         private final int minDistance = 10;    // minimum distance between location updates, in meters

         private FollowMeLocationSource() {
             // Get reference to Location Manager
             locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

             // Specify Location Provider criteria
             criteria.setAccuracy(Criteria.ACCURACY_FINE);
             //criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
             //criteria.setAltitudeRequired(false);
             //criteria.setBearingRequired(false);
             //criteria.setSpeedRequired(false);
             //criteria.setCostAllowed(true);
             
            

         }

         private void getBestAvailableProvider() {
             /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use 
              * is to ask the Location Manager for the one that best satisfies our criteria.
              * By passing the 'true' boolean we ask for the best available (enabled) provider. */
             bestAvailableProvider = locationManager.getBestProvider(criteria, true);
             
         }

         /* Activates this provider. This provider will notify the supplied listener
          * periodically, until you call deactivate().
          * This method is automatically invoked by enabling my-location layer. */
         @Override
         public void activate(OnLocationChangedListener listener) {
             // We need to keep a reference to my-location layer's listener so we can push forward
             // location updates to it when we receive them from Location Manager.
             mListener = listener;
             

             // Request location updates from Location Manager
             if (bestAvailableProvider != null) {
                 locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);
             
                 //Location location = locationManager.getLastKnownLocation(bestAvailableProvider);
                 //gMap.get
             
             } else {
                 // (Display a message/dialog) No Location Providers currently available.
             }
         }

         /* Deactivates this provider.
          * This method is automatically invoked by disabling my-location layer. */
         @Override
         public void deactivate() {
             // Remove location updates from Location Manager
             locationManager.removeUpdates(this);

             mListener = null;
         }

         @Override
         public void onLocationChanged(Location location) {
        	 
     		Log.w("DISTANCE", "CAMERA CHANGGGGGEEEEEE2");

             /* Push location updates to the registered listener..
              * (this ensures that my-location layer will set the blue dot at the new/received location) */
             if (mListener != null) {
                 mListener.onLocationChanged(location);
             }

             /* ..and Animate camera to center on that location !
              * (the reason for we created this custom Location Source !) */
             gMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
         }

         @Override
         public void onStatusChanged(String s, int i, Bundle bundle) {

         }

         @Override
         public void onProviderEnabled(String s) {

         }

         @Override
         public void onProviderDisabled(String s) {

         }
     }
  	 
     
     
     
     Thread threadGetCate = new Thread(){
         public void run(){	        	      
                 Log.w("THREAD", "DEBUT THREAD GET CATE");
             	try {
                 	Gson gson = new Gson();
                 	String url;
                 	url = Network.URL + Network.PORT + "/categories.json";        	
                 	
                 	Message myMessage, msgPb;
                 	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
                 	myHandler.sendMessage(msgPb);
                 
                 	Bundle messageBundle = new Bundle();
         			messageBundle.putInt("action", ACTION.GET_CATE.getValue());
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
         						categories = rep.getValue(Categorie.class);
         						
         					}
         					catch(JsonParseException e)
         				    {
         				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
         				    }
         					if (rep.responseCode == 1)
         					{
         						messageBundle.putInt("error", 2);
         						messageBundle.putString("msgError", rep.responseMessage);
         					}
         					else
         						Log.w("RECUP", "JAI RECUP " + categories.list.length + " categories");       					
         				}
         			}
         	       
         	        myMessage.setData(messageBundle);
                     myHandler.sendMessage(myMessage);
                     
                     msgPb = myHandler.obtainMessage(1, (Object) "Success");
                     myHandler.sendMessage(msgPb);
             	}
             	catch (Exception e) {
                     e.printStackTrace();}
             	Log.w("THREAD", "FIN THREAD GET CATE");
             }
 	};
     
     
}
