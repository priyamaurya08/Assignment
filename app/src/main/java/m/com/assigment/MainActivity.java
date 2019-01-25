package m.com.assigment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private Button add;

    private RecyclerView recyclerView;

    private TextView totalCount;

    private LinearLayoutManager linearLayoutManager;

    private InputModel inoutMode;

    private ReportsRecyclerViewAdapter reportsRecycerViewAdapter;

    private ArrayList<Result> list=new ArrayList<>();

    private Result result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add=findViewById(R.id.add_button);
        recyclerView=findViewById(R.id.recycler_view);
        totalCount=findViewById(R.id.total_reports_count);


        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        reportsRecycerViewAdapter=new ReportsRecyclerViewAdapter(list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(reportsRecycerViewAdapter);




        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String json= loadJSONFromAsset();
                inoutMode=new Gson().fromJson(json, InputModel.class);
                Intent i=new Intent(MainActivity.this,DynamicUIActivity.class);
                i.putExtra("json",inoutMode);
                startActivityForResult(i,1);

            }
        });


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("inut_json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                String json=data.getStringExtra("json");

                Log.v("TAG",json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    Iterator<String> keys= jsonObject.keys();
                    result=new Result();
                    int count=0;
                    while (keys.hasNext()) {
                        String keyValue = (String) keys.next();
                        count++;
                        if (count == 1) {
                            ResultModel resultMode = new ResultModel();
                            if (!TextUtils.isEmpty(jsonObject.getString(String.valueOf(jsonObject.names().get(0))))) {
                                resultMode.setValue(jsonObject.getString(String.valueOf(jsonObject.names().get(0))));
                                resultMode.setKey(keyValue);
                            }
                            result.setField1(resultMode);
                        } else if (count == 2) {
                            ResultModel field2 = new ResultModel();
                            if (!TextUtils.isEmpty(jsonObject.getString(String.valueOf(jsonObject.names().get(1))))) {
                                field2.setValue(jsonObject.getString(String.valueOf(jsonObject.names().get(1))));
                                field2.setKey(keyValue);
                            }
                            result.setField2(field2);
                        } else
                            break;

                    }

                    list.add(result);
                    totalCount.setText(""+list.size());
                    reportsRecycerViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
