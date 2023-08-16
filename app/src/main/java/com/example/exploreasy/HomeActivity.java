package com.example.exploreasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private ImageView imageView;
    private List<PlaceModel> dataList;
    private ItemListAdapter adapter;
    private androidx.appcompat.widget.SearchView searchView;
    public GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final String BASE_URL = "https://easyexplore123-1-z1661078.deta.app/";
    private LocationManager locationManager;
    int PERMISSION_ID = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        //Komponen untuk mencari lokasi
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        imageView = findViewById(R.id.hamburgerButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Logout Berhasil !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dataList = new ArrayList<>();

        adapter = new ItemListAdapter(dataList);
        recyclerView.setAdapter(adapter);

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            RequestBodyModel requestBody = new RequestBodyModel(location.getLatitude(), location.getLongitude());
                            fetchData(requestBody);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            RequestBodyModel requestBody = new RequestBodyModel(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            fetchData(requestBody);
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    private void fetchData(RequestBodyModel requestBody) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ResponseBody> call = apiService.postData(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        List<PlaceModel> placeList = parseJson(responseBody);
                        updateRecyclerView(placeList); //Menampilkan list data nya
                        addMarkerLocation(placeList); // Menampilkan Markers di maps
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Response Gagal !");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private List<PlaceModel> parseJson(String responseBody) throws JSONException {
        List<PlaceModel> placeList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseBody);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            double rating = jsonObject.getDouble("rating");
            String address = jsonObject.getString("address");
            String review = jsonObject.getString("review");
            String image1 = jsonObject.getString("image1");
            String image2 = jsonObject.getString("image2");
            String image3 = jsonObject.getString("image3");
            double latitude = jsonObject.getDouble("latitude");
            double longitude = jsonObject.getDouble("longitude");

            PlaceModel place = new PlaceModel(name, rating, address, review, image1, image2, image3, latitude, longitude);
            place.setName(name);
            place.setRating(rating);
            place.setAddress(address);
            place.setReview(review);
            place.setImage1(image1);
            place.setImage2(image2);
            place.setImage3(image3);
            placeList.add(place);
        }

        return placeList;
    }


    private void updateRecyclerView(List<PlaceModel> placeList) {
        dataList.clear();
        dataList.addAll(placeList);
        adapter.notifyDataSetChanged();
    }

    private void addMarkerLocation(List<PlaceModel> placeList) {
        LatLng initialPosition = new LatLng(dataList.get(0).getLatitude(), dataList.get(0).getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12));
        for(int i=0;i<placeList.size();i++){
            LatLng latLng = new LatLng(placeList.get(i).getLatitude(), placeList.get(i).getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng);
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
    }
    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                RequestBodyModel requestBody = new RequestBodyModel(latitude, longitude);
                fetchData(requestBody);
                Toast.makeText(this, "Lokasi ditemukan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
