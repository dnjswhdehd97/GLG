package com.example.glg;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonElement;

import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.DOUBLE;

public class MainActivity extends Activity {
    MyDBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;
    MyCursorAdapter myAdapter;
    String sourceLang=" ";
    double EX=0;
    final static String KEY_ID = "_id";
    final static String KEY_NAME = "name";
    final static String KEY_PRICE="price";
    final static String TABLE_NAME = "mytable";

    final static String querySelectAll = String.format("SELECT * FROM %s", TABLE_NAME);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();
        sourceLang=intent.getStringExtra("key");

        ListView list = (ListView) findViewById(R.id.listview);
        mHelper = new MyDBHelper(this);
        db = mHelper.getWritableDatabase();
        cursor = db.rawQuery(querySelectAll, null);
        final ArrayList<String> items = new ArrayList<String>() ;

        TextView sum=(TextView) findViewById(R.id.SUM);
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        String text = sf.getString("sum","0");
        Log.d("text ",text);
        sum.setText(text);

        TextView ksum=(TextView) findViewById(R.id.KSUM);
        SharedPreferences ksf = getSharedPreferences("sFile",MODE_PRIVATE);
        String ktext = ksf.getString("ksum","0");
        Log.d("ktext ",ktext);
        ksum.setText(ktext);

        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items) ;
        list.setAdapter(adapter);

        myAdapter = new MyCursorAdapter(this, cursor);
        list.setAdapter(myAdapter);

        ImageButton fab = findViewById(R.id.QRbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        final ListView listview ;

        //데이터를 저장하게 되는 리스트
        final List<String> items = new ArrayList<String>();

        super.onActivityResult(requestCode, resultCode, data);

        // QR코드/ 바코드를 스캔한 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String name = result.getContents();
        String pri;
        int i = name.indexOf(",");
        String name1 = name.substring(0, i);
        pri = name.substring(i+1);

        NaverTranslateTask asyncTask = new NaverTranslateTask();
        try {
            String name2 = asyncTask.execute(name1).get();
            String query = String.format( "INSERT INTO %s VALUES ( null, '%s','%s');", TABLE_NAME, name2, pri);
            db.execSQL(query);
            cursor = db.rawQuery( querySelectAll, null );
            myAdapter.changeCursor( cursor );


            TextView temp = (TextView) findViewById( R.id.SUM );
            String sum= temp.getText().toString();
            int j=Integer.parseInt(sum)+Integer.parseInt(pri);
            String str=Integer.toString(j);
            temp.setText(str);
            SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
            //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String text = str;// 사용자가 입력한 저장할 데이터
            Log.d("text ",text);
            editor.putString("sum",text); // key, value를 이용하여 저장하는 형태
            //최종 커밋
            editor.commit();

            if(sourceLang.equals("en")) {
                EX = 1190;
            }else if(sourceLang.equals("ja")){
                EX= 10.85;
            }else{
                EX=172.31;
            }
            TextView ktemp = (TextView) findViewById( R.id.KSUM );
            String ksum= ktemp.getText().toString();
            double k=Double.parseDouble(ksum)+(j*EX);
            String str1=Double.toString(k);
            ktemp.setText(str1);
            SharedPreferences sharedPreferences1 = getSharedPreferences("sFile",MODE_PRIVATE);
            //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
            SharedPreferences.Editor editor1= sharedPreferences1.edit();
            String text1 = str1;// 사용자가 입력한 저장할 데이터
            Log.d("text1 ",text1);
            editor1.putString("ksum",text1); // key, value를 이용하여 저장하는 형태
            editor1.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mOnClick( View v ) {
        EditText eName = (EditText) findViewById( R.id.et_name );
        String name = eName.getText().toString();
        String query = String.format( "INSERT INTO %s VALUES ( null, '%s', '-');",
                TABLE_NAME, name);
        db.execSQL( query );

        cursor = db.rawQuery( querySelectAll, null );
        myAdapter.changeCursor( cursor );

        eName.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );

    }

    public class NaverTranslateTask extends AsyncTask<String, String, String> {

        public String resultText;
        //Naver
        String clientId = "LBa55MqIYmoMe_ACd5qO";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "s7iV4BnBER";//애플리케이션 클라이언트 시크릿값";
        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.
        String targetLang = "ko";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {


            String sourceText = strings[0];

            try {
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                resultText= response.toString();
                Log.d("result" ,resultText);

                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObj = parser.parse(resultText.toString())
                        //원하는 데이터 까지 찾아 들어간다.
                        .getAsJsonObject().get("message")
                        .getAsJsonObject().get("result");
//안드로이드 객체에 담기
                TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
                return items.getTranslatedText();

            } catch (Exception e) {
                //System.out.println(e);
                Log.d("error", e.getMessage());
                return null;
            }
        }
        private class TranslatedItem {
            String translatedText;
            public String getTranslatedText() {
                return translatedText;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("result ",s);
        }

    }
}


