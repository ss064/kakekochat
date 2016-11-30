package com.example.mizuno.getjson;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

public class JsonActivity extends ActionBarActivity {
    ListView listView;
    Button b_update;
    Button b_submit;
    Button b_voice;
    public ArrayAdapter<String> arrayAdapter;
    String s_id;
    AsyncHttp asyncHttp;
    EditText comment;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        listView = (ListView) findViewById(R.id.listView);
        b_update = (Button) findViewById(R.id.update);
        b_submit = (Button) findViewById(R.id.submit);
        b_voice = (Button) findViewById(R.id.voice);
        comment = (EditText) findViewById(R.id.comment);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        //初回起動時に、Jsonを取得しListViewに貼り付ける作業を行う。
        AsyncTaskGetJson taskGetJson = new AsyncTaskGetJson(this);
        taskGetJson.execute();
        b_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTaskGetJson taskGetJson = new AsyncTaskGetJson(JsonActivity.this);
                taskGetJson.execute();
            }
        });

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncHttp = new AsyncHttp(-1,comment.getText().toString());
                asyncHttp.execute();
            }
        });

        b_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //インテント作成
                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//認識された音声を文字列として取得する
                    intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //WEB_SEARCHにするとWEB検索モードで起動できる
                    intent.putExtra(
                            RecognizerIntent.EXTRA_PROMPT,
                            "VoiceRecognitionTest");//ダイアログのタイトルを設定
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);//返却される音声認識結果の数を変える(この行は設定する必要性は薄い)
                    //インテント発行
                    startActivityForResult(intent, REQUEST_CODE);//RecognizerIntentを実行
                }catch(ActivityNotFoundException e){
                    //このインテントに応答できるアクティビティがインストールされていない場合
                    Toast.makeText(JsonActivity.this,
                            "ActivityNotFoundException", Toast.LENGTH_LONG).show();
                }
            }
        });

        //ListViewのアイテムを長押ししたら削除する
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position); //長押ししたアイテムの文字列を取得する
                Toast.makeText(getApplicationContext(), item + " delete", Toast.LENGTH_LONG).show();//トーストで表示
                s_id = item.substring(0, 4);//先頭から4文字目までIDなので、IDをs_idに格納する。
                //非同期処理を行い、Getを行う。
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //コメントのIDのdeleteコントローラーをgetすることで、アイテムが削除される。
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet("http://kakeko01.sist.ac.jp/posts/delete/" + s_id);
                            HttpResponse httpResponse = httpClient.execute(httpGet);
                            String str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                            Log.d("HTTP", str);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                }).start();
                return true;
            }
        });

        //ListViewのアイテムをタップしたら、EditTextに入ってる文字列をタップしたところに入れる(UPDATE)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);//タップしたアイテムの文字列を取得する。
                Toast.makeText(getApplicationContext(), comment.getText().toString() + " update", Toast.LENGTH_LONG).show();
                s_id = item.substring(0, 4);//タップしたアイテムのidを取得
                asyncHttp = new AsyncHttp(Integer.parseInt(s_id),comment.getText().toString());//id,コメントを送信(実はIDを指定してADDしてるだけ)
                asyncHttp.execute();//非同期処理を実行
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_json, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 自分が投げたインテントであれば応答する
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String resultsString = "";

            // 結果文字列リスト
            ArrayList<String> results;
            results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            //RecognizerIntentの音声認識結果はIntent#getStringArrayListExtra()メソッドを利用して受けとることが出来るようになっています。
            //また、音声認識の結果は1つで返却されるのではなく、複数の文字列候補を優先度(入力された音声と思われる)順に返却します
            //実装次第では、多少入力音声が乱れたり、音声解析結果がうまくいかなかった場合もあります
            // "もしかして候補"の文字列を利用することで入力精度をある程度向上させることもできるようになっています

            /*
            for (int i = 0; i< results.size(); i++) {
                // ここでは、文字列が複数あった場合に結合しています
                resultsString = results.get(i)+String.valueOf(results.size());
            }
            */
            resultsString = results.get(0);

            // トーストを使って結果を表示
            Toast.makeText(this, resultsString, Toast.LENGTH_LONG).show();
            comment.setText(resultsString);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
