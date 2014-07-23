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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
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
	
	private LocationManager locationManager;
	private GoogleMap gMap;
	private Marker posMarker;
	
	private List<Marker> listAllMarker;
	private List<Marker> listGreenMarker;
	
	private List<Place.PlaceInfo> listAllPlaceInfos;
	public Location locat = null;
	private Location centerScreen = null;
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
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        
        if (!initCheckMap())
        {
        	Toast.makeText(this, "Erreur lors du chargement de la carte", Toast.LENGTH_LONG).show();	
        	return;
        }
       //////////////////////////////////////////NEXUS ???///////////
        //ViewGroup topLayout = (ViewGroup) findViewById(R.id.map);
        //topLayout.requestTransparentRegion(topLayout);
        
        
        
        
        
        
        //SupportMapFragment mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
        //mapFragment.setTargetFragment(((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)), 1);
        
        //gMap = mapFragment.getMap();
        
        //////////////////////////////////////////////////////////////
        
      //  etLocation = (EditText) findViewById(R.id.et_location);
        
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        gMap.setOnCameraChangeListener(cc);
        
        
        
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        
        cc = new OnCameraChangeListener(){
        	@Override
        	public void onCameraChange(CameraPosition newPos)
        	{
        		if (centerScreen == null)
        			return;
        		double dist = getDistance(new LatLng(centerScreen.getLatitude(),  centerScreen.getLongitude()), newPos.target);
        		Log.w("DISTANCE", Double.toString(dist));
        		
        		/////////////////////POUR DETECT CLIC ON MY LOCATION /////////////////////////////
        		double latDelta = newPos.target.latitude;
                double lonDelta = newPos.target.longitude;  	
                Location l = gMap.getMyLocation();
                if (l != null) {
                    latDelta = Math.abs(latDelta - l.getLatitude());
                    lonDelta = Math.abs(lonDelta - l.getLongitude());
                }               
        		if (latDelta <= .000001 && lonDelta <= .000001) {
                    //Log.i("MAP", String.format("myLocation: %f,%f", latDelta, lonDelta));
                	//Toast.makeText(getApplicationContext(), "My Location clicked", Toast.LENGTH_SHORT).show();
                	abonnementWIFI();
                	abonnementGPS();
                	abonnement3G();
                	
                	//gMap.clear();  
                	Log.w("LANCE", "t5");
                	
        			
        			/*if (l != null)
        			{
        				//centerScreen.setLatitude(l.getLatitude());  ??
            			//centerScreen.setLongitude(l.getLongitude()); ??
        				new ThreadPlaces(MapView.this, new LatLng(l.getLatitude(), l.getLongitude())).start();
        			
        			}
        			else*/
        				new ThreadPlaces(MapView.this, newPos.target).start();
        				
        		}
        		//////////////////////////////////////////////////////////////////////////////
        		else
        			{
		        		//////////////////////////////////////////////////////////////
        				desabonnementGPS();
		        		desabonnement3G();
		        		desabonnementWIFI();
        				
        				if (dist > radius || listAllPlaceInfos.isEmpty())
		        		{	
		        			centerScreen.setLatitude(newPos.target.latitude);
		        			centerScreen.setLongitude(newPos.target.longitude);
		        			
		        			//listAllPlaceInfos.clear();
		        			//listAllMarker.clear();
		        			//listGreenMarker.clear();
		        					        			
		               	 	Log.w("LANCE", "t3.1");
		        			new ThreadPlaces(MapView.this, newPos.target).start();
		        		}   				
        		}
        	}	
        };
        
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
        listGreenMarker = new ArrayList<Marker>();
        listAllPlaceInfos = new ArrayList<Place.PlaceInfo>();
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        limit = 20;
        radius = 1000;     
          
    
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
        
    	if (!initCheckMap())
        {
        	Toast.makeText(this, "Erreur lors du chargement de la carte", Toast.LENGTH_LONG).show();	
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
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            abonnement3G();
        }
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        desabonnementGPS();
        desabonnementWIFI();
        desabonnement3G();
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
    	Log.w("MAP", "iciiiiiiiiiiii wifi");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
    }
    
    public void abonnement3G() {
        //On s'abonne
    	Log.w("MAP", "iciiiiiiiiiiii 3g");
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
    	//Toast.makeText(getApplicationContext(), "ON LOCATION_CHANGE", Toast.LENGTH_SHORT).show();
    	
    	item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
  		item_loading.setVisible(true);
    	
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
            /*else
            {
           	 	Log.w("LANCE", "t2");
            	new ThreadPlaces(this, new LatLng(locat.getLatitude(), locat.getLongitude())).start();
            }*/
        }
    }
 
    @Override
    public void onProviderDisabled(final String provider) {
        //Si le GPS est désactivé on se désabonne
    	// Log.w("Map", "iciiiiiiiiiiii8");
        if("GPS_PROVIDER".equals(provider))
            desabonnementGPS();
        if("NETWORK_PROVIDER".equals(provider))
            desabonnementWIFI();
        if("PASSIVE_PROVIDER".equals(provider))
            desabonnement3G();
    }
 
    @Override
    public void onProviderEnabled(final String provider) {
    	Log.d("Map", "Provider = " + provider);
    	if("GPS_PROVIDER".equals(provider))
            abonnementGPS();
        if("NETWORK_PROVIDER".equals(provider))
        	abonnementWIFI();
        if("PASSIVE_PROVIDER".equals(provider))
            abonnement3G();
    }
    
    //  onMyLocation not implemented yet in the api
   /* public void OnMyLocationButtonClickListener() {
    	Toast.makeText(getApplicationContext(), "ICIIII   la", Toast.LENGTH_SHORT).show();

    }*/
    
 
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
							                desabonnementGPS();
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
    			    			places.list[indice].marker.setIcon(BitmapDescriptorFactory.fromBitmap(places.list[indice].bitmap));	    		
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
    				places.list[i].markerDef = gMap.addMarker(new MarkerOptions().position(new LatLng(places.list[i].lat, places.list[i].lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin1)));
    				places.list[i].marker = gMap.addMarker(new MarkerOptions().title(places.list[i].name).position(new LatLng(places.list[i].lat, places.list[i].lon)).snippet(places.list[i].address).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin1)));
    				
    				if (places.list[i].markerDef == null)
    					Log.w("MAP", "MARDEF null");
    				else 
    				{
    					//places.list[i].markerDef.hideInfoWindow();
    					//places.list[i].markerDef.setDraggable(false);
    					//places.list[i].markerDef.setFlat(true);
        				//places.list[i].markerDef.setInfoWindowAnchor(-1, -1);
        				//places.list[i].markerDef.setAnchor(-1, -1);
    					listGreenMarker.add(places.list[i].markerDef);
    				}
    				if (places.list[i].marker == null)
    					Log.w("MAP", "MARK null");
    				else 
    				{
    					listAllMarker.add(places.list[i].marker);
    				}
    				
    				
    				new ThreadDownloadImage(MapView.this, i).start();
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
				
				m = listGreenMarker.get(0);
				m.remove();		
				listGreenMarker.remove(0);
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
    	    if (gMap == null) {
    	    	
    	    	SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    	    	
    	    	//CustomMapFragment mapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    	    	
    	    	//CustomMapFragment mapFragment = (CustomMapFragment) CustomMapFragment.newInstance(new GoogleMapOptions().zOrderOnTop(true));
    	        //mapFragment.setTargetFragment(((CustomMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)), 1);
    	    	
    	    	
    	    	gMap = mapFragment.getMap();
    	    	
    	    	//gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    	        if (gMap != null)
    	            return true;    	       
    	        return false;
    	    }
    	    return true;
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
}
