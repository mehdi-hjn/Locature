package com.ensa.locature.main.Map;
import com.ensa.locature.main.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.map_fragment,container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mMapView=(MapView) mView.findViewById(R.id.map);
            if(mMapView != null){
                mMapView.onCreate(null);
                mMapView.onResume();
                mMapView.getMapAsync(this);
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap=googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    public void placeMarker(String title, String snippet, double lat, double lon) {
        if (mGoogleMap != null) {
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(title).snippet(snippet));
        }
    }

    public void placeCamera(double lat, double lon){
        CameraPosition Position = CameraPosition.builder().target(new LatLng(lat,lon)).zoom(16).bearing(0).tilt(45).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(Position) );
    }


}
