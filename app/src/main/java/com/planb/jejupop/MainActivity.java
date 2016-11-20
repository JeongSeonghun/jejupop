package com.planb.jejupop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends NMapActivity
        implements NMapView.OnMapStateChangeListener, NMapOverlayManager.OnCalloutOverlayListener {
    TextView textView_shortWeather;
    TextView addr_text;
    String[] loc={"서울", "이도이동", "건입동", "구좌음", "노형동", "도두동", "봉개동",
            "삼도1동", "삼도2동", "삼양동", "아라동"};
    Spinner spinner;
    ArrayList<String> list=new ArrayList<String>();
    int select;

    ArrayList<String> titlelist= new ArrayList<String>(); // 리스트 뷰에 출력할 타이틀 제목들;
    ArrayAdapter<String> adapter;
    ListView tlistview;

    phpDown task;

    ArrayList<ListItem> listItem= new ArrayList<ListItem>();
    ListItem Item;

    //naver 지도 api
    // API-KEY
    public static final String API_KEY = "rpETzrhVXw2vxQ7RiF9F";  //<---맨위에서 발급받은 본인 ClientID 넣으세요.
    // 네이버 맵 객체
    NMapView mMapView = null;
    // 맵 컨트롤러
    NMapController mMapController = null;
    // 맵을 추가할 레이아웃
    LinearLayout MapContainer;

    //추가1 , NMapcallout BasicOverlay/CustomOldOverlay, NMapPOIflagType, NMapViewerResourceProvidder

    // 오버레이의 리소스를 제공하기 위한 객체
//	NMapViewerResourceProvider mMapViewerResourceProvider = null;

    // 오버레이 관리자
    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;


    //test
   // private NMapOverlayManager mOverlayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_shortWeather = (TextView)findViewById(R.id.weat);
        tlistview= (ListView)findViewById(R.id.list);

        checkDangerousPermissions();

        // 날씨관련
        new ReceiveShortWeather().execute();
        spinner= (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,loc);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String number= list.get(i);
                if(i==0){
                    select=0;
                }else if(i==1){
                    select=1;
                }else if(i==2){
                    select=2;
                }else if(i==3){
                    select=3;
                }else if(i==4){
                    select=4;
                }else if(i==5){
                    select=5;
                }else if(i==6){
                    select=6;
                }else if(i==7){
                    select=7;
                }else if(i==8){
                    select=8;
                }else if(i==9){
                    select=9;
                }else if(i==10){
                    select=10;
                }else{
                    select=0;
                }
                new ReceiveShortWeather().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //날씨 관련 끝

        //json관련
        task = new phpDown();
        task.execute("http://shun6889.ipdisk.co.kr:8000/xe/test/jejupopcon.php");

        //네이버
// 네이버 지도를 넣기 위한 LinearLayout 컴포넌트
        MapContainer = (LinearLayout) findViewById(R.id.mapCon);
        // 네이버 지도 객체 생성
        mMapView = new NMapView(this);
        // 지도 객체로부터 컨트롤러 추출
        mMapController = mMapView.getMapController();
        // 네이버 지도 객체에 APIKEY 지정
        mMapView.setApiKey(API_KEY);
        // 생성된 네이버 지도 객체를 LinearLayout에 추가시킨다.
        MapContainer.addView(mMapView);
        // 지도를 터치할 수 있도록 옵션 활성화
        mMapView.setClickable(true);
        // 확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
        mMapView.setBuiltInZoomControls(true, null);
        // 지도에 대한 상태 변경 이벤트 연결
        mMapView.setOnMapStateChangeListener(this);

        // set data provider listener, 주소 표시하기위해 사용
        super.setMapDataProviderListener(onDataProviderListener);

    }

    //비동기 날씨
    public class ReceiveShortWeather extends AsyncTask<URL, Integer, Long> {

        ArrayList<ShortWeather> shortWeathers = new ArrayList<ShortWeather>();

        protected Long doInBackground(URL... urls) {

            String url;
            String url0 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1159068000"; //원본 서울, rss 주소
            String url1 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011054000"; //test 이도이동
            String url2 = "http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011059000"; //건입동
            String url3 = "http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011025600"; //구좌읍
            String url4 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011066000"; //노형동
            String url5 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011069000"; //도두동
            String url6 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011062000"; //봉개동
            String url7 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011055000"; //삼도1동
            String url8 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011056000"; //삼도2동
            String url9 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011061000"; //삼양동
            String url10 = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=5011063000"; //아라동


            if (select == 0) {
                url=url0;
            }else if(select==1){
                url=url1;
            }else if(select==2){
                url=url2;
            }else if(select==3){
                url=url3;
            }else if(select==4){
                url=url4;
            }else if(select==5){
                url=url5;
            }else if(select==6){
                url=url6;
            }else if(select==7){
                url=url7;
            }else if(select==8){
                url=url8;
            }else if(select==9){
                url=url9;
            }else if(select==10){
                url=url10;
            }else {
                url=url0;
            }
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;

            try {
                response = client.newCall(request).execute();
                parseXML(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        //임시 날씨
        protected void onPostExecute(Long result){
            String data = "";
            data= shortWeathers.get(0).getWfKor()+"\n"+shortWeathers.get(0).getTemp()+"℃";
            textView_shortWeather.setText(data);
        }
        //전체날씨
        /*
        protected void onPostExecute(Long result) {
            String data = "";

            for(int i=0; i<shortWeathers.size(); i++) {
                data += shortWeathers.get(i).getHour() + " " +
                        shortWeathers.get(i).getDay() + " " +
                        shortWeathers.get(i).getTemp() + " " +
                        shortWeathers.get(i).getWfKor() + " " +
                        shortWeathers.get(i).getPop() + "\n";
            }//시간 날짜 온도 날씨 강수확률

            textView_shortWeather.setText(data);
        }
        */

        //xml파싱
        void parseXML(String xml) {
            try {
                String tagName = "";
                boolean onHour = false;
                boolean onDay = false;
                boolean onTem = false;
                boolean onWfKor = false;
                boolean onPop = false;
                boolean onEnd = false;
                boolean isItemTag1 = false;
                int i = 0;

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(new StringReader(xml));

                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        tagName = parser.getName();
                        if (tagName.equals("data")) {
                            shortWeathers.add(new ShortWeather());
                            onEnd = false;
                            isItemTag1 = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT && isItemTag1) {
                        if (tagName.equals("hour") && !onHour) {
                            shortWeathers.get(i).setHour(parser.getText());
                            onHour = true;
                        }
                        if (tagName.equals("day") && !onDay) {
                            shortWeathers.get(i).setDay(parser.getText());
                            onDay = true;
                        }
                        if (tagName.equals("temp") && !onTem) {
                            shortWeathers.get(i).setTemp(parser.getText());
                            onTem = true;
                        }
                        if (tagName.equals("wfKor") && !onWfKor) {
                            shortWeathers.get(i).setWfKor(parser.getText());
                            onWfKor = true;
                        }
                        if (tagName.equals("pop") && !onPop) {
                            shortWeathers.get(i).setPop(parser.getText());
                            onPop = true;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (tagName.equals("s06") && onEnd == false) {
                            i++;
                            onHour = false;
                            onDay = false;
                            onTem = false;
                            onWfKor = false;
                            onPop = false;
                            isItemTag1 = false;
                            onEnd = true;
                        }
                    }

                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class phpDown extends AsyncTask<String, Integer,String>{
        @Override

        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }

            } catch(Exception ex){
                ex.printStackTrace();

            }

            return jsonHtml.toString();
        }


        protected void onPostExecute(String str){
            String title;
            String content;
            String imgname;

            try{
                JSONObject root = new JSONObject(str);

                JSONArray ja = root.getJSONArray("results");

                for(int i=0; i<ja.length(); i++){

                    JSONObject jo = ja.getJSONObject(i);

                    title = jo.getString("title");
                    content = jo.getString("content");
                    imgname = jo.getString("imgname");

                    listItem.add(new ListItem(title,content,imgname));

                    titlelist.add(title);
                }

            }catch(JSONException e){

                e.printStackTrace();

            }

// listView 관련

            adapter= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titlelist);
            tlistview.setAdapter(adapter);
            tlistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parant, View view, int position, long ld) {
                    //명시적 활성화에 사용할 인텐트 생성
                    Intent intent= new Intent(getApplicationContext(),ContentActivity.class);
                    System.out.print("보냄"+position);

                    intent.putExtra("R_num", String.valueOf(position));

                    //새 액티비티 시작
                    startActivity(intent);
                }
            });
        }

    }


    //naver api
    /**
     * 지도가 초기화된 후 호출된다.
     * 정상적으로 초기화되면 errorInfo 객체는 null이 전달되며,
     * 초기화 실패 시 errorInfo객체에 에러 원인이 전달된다
     */
    @Override
    public void onMapInitHandler(NMapView mapview, NMapError errorInfo) {
        if (errorInfo == null) { // success
            //mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
            startMyLocation();//현재위치로 이동
            System.out.println("test: "+mMapController.getMapCenter().toString());
            //findPlacemarkAtLocation(new NGeoPoint(126.978371, 37.5666091).getLongitude(), new NGeoPoint(126.978371, 37.5666091).latitude);


        } else { // fail
            android.util.Log.e("NMAP", "onMapInitHandler: error="
                    + errorInfo.toString());
        }
    }

    /**
     * 지도 레벨 변경 시 호출되며 변경된 지도 레벨이 파라미터로 전달된다.
     */
    @Override
    public void onZoomLevelChange(NMapView mapview, int level) {}

    /**
     * 지도 중심 변경 시 호출되며 변경된 중심 좌표가 파라미터로 전달된다.
     */
    @Override
    public void onMapCenterChange(NMapView mapview, NGeoPoint center) {

    }

    /**
     * 지도 애니메이션 상태 변경 시 호출된다.
     * animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
     * animState : ANIMATION_STATE_STARTED or ANIMATION_STATE_FINISHED
     */
    @Override
    public void onAnimationStateChange(
            NMapView arg0, int animType, int animState) {}

    @Override
    public void onMapCenterChangeFine(NMapView arg0) {}


    private void startMyLocation() {
        //mMapLocationMager
        //단말기의 현재 위치 탐색 기능을 사용하기 위한 클래스입니다.
        //내부적으로 시스템에서 제공하는 GPS및 네트워크를 모두 사용하여 현재 위치를 탐색합니다.
        mMapLocationManager = new NMapLocationManager(this);
        //NMapLocationManager(Context context)
        mMapLocationManager
                .setOnLocationChangeListener(onMyLocationChangeListener);


        boolean isMyLocationEnabled = mMapLocationManager
                .enableMyLocation(true);

        if (!isMyLocationEnabled) {

            Toast.makeText(
                    MainActivity.this,
                    "Please enable a My Location source in system settings",
                    Toast.LENGTH_LONG).show();


            Intent goToSettings = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(goToSettings);
            finish();

        }else{

        }
//
//		// compass manager
//		mMapCompassManager = new NMapCompassManager(this);
//
//		// create my location overlay
//		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(
//				mMapLocationManager, mMapCompassManager);
//
//		if (mMyLocationOverlay != null) {
//			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
//				mOverlayManager.addOverlay(mMyLocationOverlay);
//			}
//
//			if (mMapLocationManager.isMyLocationEnabled()) {
//
//				if (!mMapView.isAutoRotateEnabled()) {
//					mMyLocationOverlay.setCompassHeadingVisible(true);
//
//					mMapCompassManager.enableCompass();
//
//					mMapView.setAutoRotateEnabled(true, false);
//
//					MapContainer.requestLayout();
//				} else {
//					stopMyLocation();
//				}
//
//				mMapView.postInvalidate();
//			} else {
//				boolean isMyLocationEnabled = mMapLocationManager
//						.enableMyLocation(true);
//				if (!isMyLocationEnabled) {
//					Toast.makeText(
//							NaverMyLocation.this,
//							"Please enable a My Location source in system settings",
//							Toast.LENGTH_LONG).show();
//
//					Intent goToSettings = new Intent(
//							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//					startActivity(goToSettings);
//
//					return;
//				}
//			}
//		}
    }

    private void stopMyLocation() {

        if (mMyLocationOverlay != null) {

            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {

                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                MapContainer.requestLayout();

            }
        }
    }


    private final NMapLocationManager.OnLocationChangeListener
            onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager,
                                         NGeoPoint myLocation) {

            // 현제 위치 지도 표시
			if (mMapController != null) {
                mMapController.animateTo(myLocation);
                mMapController.setZoomLevel(8);

            }

            Log.d("myLog", "myLocation  lat " + myLocation.getLatitude());
            Log.d("myLog", "myLocation  lng " + myLocation.getLongitude());

            findPlacemarkAtLocation(myLocation.getLongitude(), myLocation.getLatitude());
            //위도경도를 주소로 변환
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            // Runnable runnable = new Runnable() {
            // public void run() {
            // stopMyLocation();
            // }
            // };
            // runnable.run();


            Toast.makeText(MainActivity.this,
                    "Your current location is temporarily unavailable.",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(
                NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(MainActivity.this,
                    "Your current location is unavailable area.",
                    Toast.LENGTH_LONG).show();

            stopMyLocation();
        }
    };


    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {
        //findPlacemarkAtLocation 사용시 호출됨
        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            if (errInfo != null) {

                Log.e("myLog", "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());
                Toast.makeText(MainActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                return;

            }else{
                Toast.makeText(MainActivity.this, placeMark.toString(), Toast.LENGTH_LONG).show();
                System.out.println("test2: "+placeMark.toString());
                if(placeMark.dongName.toString().equals("이도2동")){
                        select=1;
                    new ReceiveShortWeather().execute();
                    addr_text=(TextView)findViewById(R.id.addr_t);
                    addr_text.setText(placeMark.dongName.toString());
                }

            }
        }
    };

    @Override
    public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
                                                     NMapOverlayItem arg1, Rect arg2) {
        return null;
    }




    //권한 확인
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
