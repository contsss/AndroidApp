package com.example.in0.nodoublecontract;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class FragmentSearchAddress extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Geocoder geocoder;
    private TextView idSearch;
    private EditText etZip;
    private EditText etAddress;
    private EditText etDetailAddress;
    private TextView tvCategory;
    private String session_id;
    private String zip;
    private String address1;
    private String address2;
    private String category_value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_search_address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        etZip = findViewById(R.id.zip);
        etAddress = findViewById(R.id.address);
        etDetailAddress = findViewById(R.id.detailAddress);
        tvCategory = findViewById(R.id.category_value);
        idSearch = findViewById(R.id.id_search);

        Intent intent = new Intent(this.getIntent());
        category_value = intent.getStringExtra("category_value");
        session_id = intent.getStringExtra("id");
        zip = intent.getStringExtra("zip");
        address1 = intent.getStringExtra("address1");
        address2 = intent.getStringExtra("address2");
        tvCategory.setText(category_value);
        idSearch.setText(session_id);

        if(zip != null && address1 != null && address2 != null) {
            etZip.setText(zip);
            etAddress.setText(address1 + " " + address2);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        LatLng seoul = new LatLng(37.566695, 126.978020);
        mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (zip != null) {
                    String str = etAddress.getText().toString();
                    List<Address> addressList = null;
                    try {
                        // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                        addressList = geocoder.getFromLocationName(
                                str, // 주소
                                10); // 최대 검색 결과 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println(addressList.get(0).toString());
                    // 콤마를 기준으로 split
                    String[] splitStr = addressList.get(0).toString().split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
                    System.out.println(address);

                    String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                    String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                    System.out.println(latitude);
                    System.out.println(longitude);

                    // 좌표(위도, 경도) 생성
                    LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title("등록하기");
                    mOptions2.snippet(address);
                    mOptions2.position(position);
                    // 마커 추가
                    mMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                }
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
               insertForSaleFacade(marker);
            }
        });
    }
    public void searchAddress(View view) {
        Intent intent = new Intent(this, DaumWebViewActivity.class);
        TextView textView = findViewById(R.id.category_value);
        category_value = textView.getText().toString();
        intent.putExtra("category", category_value); // 0:구매자 모드, 1:판매자 모드
        startActivity(intent);
    }

    public void insertForSaleFacade(Marker marker) {
        marker.setVisible(false);
        String Id = idSearch.getText().toString();
        String Address = etAddress.getText().toString();
        String DetailAddress = etAddress.getText().toString();

        insertForSale(Id, Address, DetailAddress);

        Intent intent = new Intent(getBaseContext(), ListForSaleActivity.class);
        String address = address1 + " " + address2;
        String detailAddress = etDetailAddress.getText().toString();
        intent.putExtra("address", address);
        intent.putExtra("detailAddress", detailAddress);
        insertForSale(session_id, address, detailAddress); // , address, detailAddress
        startActivity(intent);
    }

    private void insertForSale(String Id, String Address, String DetailAddress) { //
        class InsertForSaleData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FragmentSearchAddress.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Id = params[0];
                    String Address = params[1];
                    String DetailAddress = params[2];

                    String link = "http://it_da.iptime.org:9659/it_da/up_da_regist_for_sale.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Address", "UTF-8") + "=" + URLEncoder.encode(Address, "UTF-8");
                    data += "&" + URLEncoder.encode("DetailAddress", "UTF-8") + "=" + URLEncoder.encode(DetailAddress, "UTF-8");

                    URL url;
                    url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertForSaleData task = new InsertForSaleData();
        task.execute(Id, Address, DetailAddress); //
    }
}
