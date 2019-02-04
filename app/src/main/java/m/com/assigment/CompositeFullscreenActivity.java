package m.com.assigment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompositeFullscreenActivity extends AppCompatActivity {
    private Fields inoutMode;

    private String TAG ="CompositeFullscreenActivity" ;

    private LinearLayout linearLayout;

    private LinearLayout.LayoutParams layoutParams;

    private Button done;

    private ArrayList<View> viewArrayList =new ArrayList<>();

    private ArrayList<Spinner> spinnerList=new ArrayList<>();

    private ArrayList<String> values=new ArrayList<>();

    private EditText editText;

    private Button button;

    private boolean isTrue=true;

    final JSONObject jsonObject = new JSONObject();

    JSONObject jsonObjectLocal = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composite_fullscreen);

        done=findViewById(R.id.done);

        try {
            inoutMode = getIntent().getParcelableExtra("json");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        if(inoutMode!=null){
            done.setVisibility(View.VISIBLE);
        }else {
            done.setVisibility(View.GONE);
            Toast.makeText(this,"No input data for dynamic UI",Toast.LENGTH_SHORT).show();
        }

        layoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,30,0,0);


        linearLayout= (LinearLayout) findViewById(R.id.container);
        for (Fields f: inoutMode.getFields()) {
            if (f.getType().equals("text")) {
                setLabel(f.getFieldName());
                setEditText(f);
            } else if (f.getType().equals("number")) {
                //create a button similarly as above,and add it to the layout
                setLabel(f.getFieldName());
                setEditText(f);
            }else if (f.getType().equals("dropdown")) {
                setLabel(f.getFieldName());
                setSpinner(f);
                //create a button similarly as above,and add it to the layout
            }else if (f.getType().equals("multiline")) {
                setLabel(f.getFieldName());
                setEditText(f);
                //create a button similarly as above,and add it to the layout
            }else if (f.getType().equals("composite")) {
                setLabel(f.getFieldName());
                setAddButton(f);
                //setComposite(f);
                //create a button similarly as above,and add it to the layout
            }


        }


        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Log.v(TAG, String.valueOf(isValid()));
                if (isValid()) {
                    for (int i = 0; i < viewArrayList.size(); i++) {
                        if (viewArrayList.get(i) instanceof EditText) {
                            EditText editText = (EditText) viewArrayList.get(i);
                            if (!TextUtils.isEmpty(editText.getText().toString())) {
                                try {
                                    jsonObject.put(editText.getTag().toString(), editText.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (viewArrayList.get(i) instanceof Spinner) {
                            Spinner spinner = (Spinner) viewArrayList.get(i);
                            String value = (String) spinner.getSelectedItem();
                            try {
                                jsonObject.put(spinner.getTag().toString(), value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else if (viewArrayList.get(i) instanceof Button) {
                            try {
                                if(jsonObjectLocal!=null){
                                    jsonObject.put("fields", jsonObjectLocal);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    Log.v(TAG, jsonObject.toString());

                    Intent i = new Intent();  // or // Intent i = getIntent()
                    i.putExtra("json", jsonObject.toString());
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });


    }

    public boolean isValid(){
        for (Fields f: inoutMode.getFields()) {
            for (int i = 0; i < viewArrayList.size(); i++) {
                if (viewArrayList.get(i) instanceof EditText) {
                    EditText editText = (EditText) viewArrayList.get(i);

                    if (editText.getTag().equals(f.getFieldName())) {
                        if (TextUtils.isEmpty(editText.getText().toString()) && f.isRequired()) {
                            //Log.v(TAG, String.valueOf(editText.getTag()));
                            Toast.makeText(this, "The " + f.getFieldName() + " is required", Toast.LENGTH_SHORT).show();
                            isTrue = false;
                            break;
                        } else {
                            isTrue = true;

                        }

                        if (editText.getTag().equals("number")) {
                            if (f.getMin() != 0 && Integer.parseInt(editText.getText().toString()) < f.getMin()) {
                                Toast.makeText(this, "The value in " + f.getFieldName() + " is less then minimum value", Toast.LENGTH_SHORT).show();
                                isTrue = false;
                                break;
                            } else {
                                isTrue = true;

                            }
                            if (f.getMax() != 0 && Integer.parseInt(editText.getText().toString()) > f.getMax()) {
                                Toast.makeText(this, "The value in " + f.getFieldName() + "is less then maximum value", Toast.LENGTH_SHORT).show();
                                isTrue = false;
                                break;
                            } else {
                                isTrue = true;

                            }
                        }
                    }
                    if (!isTrue)
                        break;
                } else if (viewArrayList.get(i) instanceof Spinner) {
                    Spinner spinner = (Spinner) viewArrayList.get(i);
                    int value = spinner.getSelectedItemPosition();
                    if (spinner.getTag().equals(f.getFieldName())) {
                        if (value == 0) {
                            Toast.makeText(this, "Please select data in " + f.getFieldName(), Toast.LENGTH_SHORT).show();
                            isTrue = false;
                            break;

                        } else {
                            isTrue = true;

                        }
                    }

                    if (!isTrue)
                        break;
                }else if(viewArrayList.get(i) instanceof Button ){
                    Log.v(TAG, String.valueOf(f.getType()));

                    if(f.getType().equals("composite") & f.isRequired()){
                        Log.v(TAG, String.valueOf(f.getType())+ "i");

                        if(jsonObjectLocal==null){
                            Log.v(TAG, String.valueOf(f.getType())+ "ij");
                            Toast.makeText(this,"Please add data in "+f.getFieldName(),Toast.LENGTH_SHORT).show();
                            isTrue=false;
                            break;
                        }else {
                            isTrue=true;
                        }
                    }
                    if(!isTrue)
                        break;
                }
            }
            if(!isTrue)
                break;
        }
        if(isTrue)
            return true;
        else
            return false;
    }





    public void setLabel(String labelName){
        TextView txtView = new TextView(this);
        txtView.setText(labelName.substring(0,1).toUpperCase()+labelName.substring(1)+":");
        txtView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtView.setTextSize(18);
        txtView.setLayoutParams(layoutParams);
        linearLayout.addView(txtView);
    }

    public void setEditText(Fields fields){

        editText= new EditText(this);
        editText.setLayoutParams(layoutParams);
        editText.setTag(fields.getFieldName());
        editText.setPadding(30,20,30,20);
        editText.setBackground(getDrawable(R.drawable.shape_reactangle_black));

        switch (fields.getType()){
            case "text":
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "number":
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "multiline":
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editText.setLines(6);
                editText.setGravity(Gravity.TOP|Gravity.LEFT);
                break;
        }

        viewArrayList.add(editText);
        linearLayout.addView(editText);
    }
    public void setAddButton(final Fields fields){
         button=new Button(this);
        button.setLayoutParams(layoutParams);
        button.setText("ADD "+fields.getFieldName());
        button.setTag(fields.getFieldName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(CompositeFullscreenActivity.this,CompositeFullscreenActivity.class);
                i.putExtra("json",fields);
                startActivityForResult(i,1);
            }
        });
        viewArrayList.add(button);
        linearLayout.addView(button);
    }

    public void setSpinner(Fields fields){
        LinearLayout linearLayout1=new LinearLayout(this);
        Spinner spinner = new Spinner(this);

        spinner.setTag(fields.getFieldName());
        LinearLayout.LayoutParams layoutParams1 =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        linearLayout1.setBackground(getDrawable(R.drawable.shape_reactangle_black));
        //Sample String ArrayList
        linearLayout1.setLayoutParams(layoutParams);
        spinner.setLayoutParams(layoutParams1);
        fields.getOptions().add(0,"Select value from drop down");
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item,fields.getOptions());
        spinner.setAdapter(adp);

        //Set listener Called when the item is selected in spinner

        viewArrayList.add(spinner);

        linearLayout1.addView(spinner);
        linearLayout.addView(linearLayout1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if (requestCode == 1) {
                String json = data.getStringExtra("json");
                Log.v("TAG", json);
                try {
                    jsonObjectLocal = new JSONObject(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
