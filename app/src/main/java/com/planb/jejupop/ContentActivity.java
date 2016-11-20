package com.planb.jejupop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class ContentActivity extends AppCompatActivity {
    ImageView image;
    TextView content, title;
    String url;
    phpDown task;
    ArrayList<ListItem> listItem= new ArrayList<ListItem>();
    ListItem Item;
    String str1;
    int strint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        image= (ImageView)findViewById(R.id.imageView);
        title= (TextView)findViewById(R.id.titlecon);
        content= (TextView)findViewById(R.id.contentcon);

        Intent intent = getIntent();
        str1 = intent.getExtras().getString("R_num");
        System.out.print("설명: "+str1);
        strint= Integer.valueOf(str1);

        task = new phpDown();
        task.execute("http://shun6889.ipdisk.co.kr:8000/xe/test/jejupopcon.php");

    }
    public class ImageLoaderTask extends AsyncTask<Void, Void, Bitmap> {

        /** The target image view to load an image */
        private ImageView imageView;

        /** The address where an image is stored. */
        private String imageAddress;


        public ImageLoaderTask(ImageView imageView, String imageAddress) {
            this.imageView = imageView;
            this.imageAddress = imageAddress;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                InputStream is = new java.net.URL(this.imageAddress).openStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e("ImageLoaderTask", "Cannot load image from " + this.imageAddress);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.imageView.setImageBitmap(bitmap);
        }
    }//[출처] [안드로이드] URL로부터 이미지를 ImageView로 로딩하는 방법|작성자 도모네

    private class phpDown extends AsyncTask<String, Integer,String> {
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

        protected void onPostExecute(String str){;;
            String imgurl;
            String txt1;
            String txt2;
            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    imgurl = jo.getString("title");
                    txt1 = jo.getString("content");
                    txt2 = jo.getString("imgname");
                    listItem.add(new ListItem(imgurl,txt1,txt2));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            url="http://shun6889.ipdisk.co.kr:8000/xe/image/"+listItem.get(strint).getData(2)+".jpg";
            new ImageLoaderTask((ImageView)findViewById(R.id.imageView), url).execute();
            title.setText(listItem.get(strint).getData(0));
            content.setText(listItem.get(strint).getData(1));
        }

    }
}
