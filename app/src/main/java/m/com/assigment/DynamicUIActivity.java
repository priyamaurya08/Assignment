package m.com.assigment;

import android.content.Intent;
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

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DynamicUIActivity extends AppCompatActivity {

    private String TAG="DynamicUIActivity";
    private InputModel inoutMode;

    private LinearLayout linearLayout;

    private LinearLayout.LayoutParams layoutParams;

    private Button done;

    private ArrayList<View> viewArrayList =new ArrayList<>();

    private ArrayList<View> viewArrayListComposite =new ArrayList<>();

    private ArrayList<Spinner> spinnerList=new ArrayList<>();

    private ArrayList<String> values=new ArrayList<>();

    private  EditText editText;

    private boolean isTrue=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_ui);

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
        for (Fields f: inoutMode.getData()) {
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
                setComposite(f);
                //create a button similarly as above,and add it to the layout
            }


        }


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Log.v(TAG, String.valueOf(isValid()));
                if (isValid()) {
                    final JSONObject jsonObject = new JSONObject();
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
                        }else if(viewArrayList.get(i) instanceof  LinearLayout){
                            JSONObject jsonObjectChild=null;
                            JSONArray jsonArray=new JSONArray();
                            LinearLayout linearLayout= (LinearLayout) viewArrayList.get(i);
                            for (int j = 0; j < viewArrayListComposite.size(); j++) {
                                jsonObjectChild= new JSONObject();
                                if (viewArrayListComposite.get(j) instanceof EditText) {
                                    EditText editText = (EditText) viewArrayListComposite.get(j);
                                    if (!TextUtils.isEmpty(editText.getText().toString())) {
                                        try {
                                            jsonObjectChild.put(editText.getTag().toString(), editText.getText().toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (viewArrayListComposite.get(j) instanceof Spinner) {
                                    Spinner spinner = (Spinner) viewArrayListComposite.get(j);
                                    String value = (String) spinner.getSelectedItem();
                                    try {
                                        jsonObjectChild.put(spinner.getTag().toString(), value);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                jsonArray.put(jsonObjectChild);
                            }

                            try {
                                jsonObject.put("fields",jsonArray);
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
        for (Fields f: inoutMode.getData()) {
            for (int i = 0; i < viewArrayList.size(); i++) {
                if (viewArrayList.get(i) instanceof EditText) {
                    EditText editText = (EditText) viewArrayList.get(i);

                    if(editText.getTag().equals(f.getFieldName())) {
                        if (TextUtils.isEmpty(editText.getText().toString()) && f.isRequired()) {
                            Log.v(TAG, String.valueOf(editText.getTag()));
                            Toast.makeText(this,"The "+f.getFieldName()+" is required",Toast.LENGTH_SHORT).show();
                            isTrue=false;
                            break;
                        }else {
                            isTrue=true;

                        }

                        if(editText.getTag().equals("number")) {
                            if (f.getMin() != 0 && Integer.parseInt(editText.getText().toString()) < f.getMin()) {
                                Toast.makeText(this, "The value in "+f.getFieldName()+" is less then minimum value", Toast.LENGTH_SHORT).show();
                                isTrue = false;
                                break;
                            }else {
                                isTrue=true;

                            }
                            if (f.getMax() != 0 && Integer.parseInt(editText.getText().toString()) > f.getMax()) {
                                Toast.makeText(this, "The value in "+f.getFieldName()+"is less then maximum value", Toast.LENGTH_SHORT).show();
                                isTrue = false;
                                break;
                            }else {
                                isTrue=true;

                            }
                        }
                    }
                    if(!isTrue)
                        break;
                } else if (viewArrayList.get(i) instanceof Spinner) {
                    Spinner spinner = (Spinner) viewArrayList.get(i);
                    int value =  spinner.getSelectedItemPosition();
                    if(spinner.getTag().equals(f.getFieldName())) {
                        if (value==0 ) {
                            Toast.makeText(this,"Please select data in "+f.getFieldName(),Toast.LENGTH_SHORT).show();
                            isTrue=false;
                            break;

                        }else {
                            isTrue=true;

                        }
                    }

                    if(!isTrue)
                        break;
                }else if(viewArrayList.get(i) instanceof LinearLayout){
                    LinearLayout linearLayout= (LinearLayout) viewArrayList.get(i);
                    Log.v(TAG,"Linear Layout"+ String.valueOf(linearLayout.getTag()));
                    if(linearLayout.getTag().equals(f.getFieldName()) ) {
                        for (Fields fields: f.getFields()) {

                            for (int j = 0; j < viewArrayListComposite.size(); j++) {
                                if (viewArrayListComposite.get(j) instanceof EditText) {
                                    EditText editText = (EditText) viewArrayListComposite.get(j);

                                    if(editText.getTag().equals(fields.getFieldName())) {
                                        if (TextUtils.isEmpty(editText.getText().toString()) && fields.isRequired()) {
                                            Toast.makeText(this,"The "+fields.getFieldName()+" is required",Toast.LENGTH_SHORT).show();
                                            isTrue=false;
                                            break;
                                        }else {
                                            isTrue=true;
                                        }
                                        if(editText.getTag().equals("number")) {
                                            if (fields.getMin() != 0 && Integer.parseInt(editText.getText().toString()) < fields.getMin()) {
                                                Toast.makeText(this, "The value  in "+fields.getFieldName()+"is less then minimum value", Toast.LENGTH_SHORT).show();
                                                isTrue = false;
                                                break;
                                            }else {
                                                isTrue=true;

                                            }
                                            if (fields.getMax() != 0 && Integer.parseInt(editText.getText().toString()) > fields.getMax()) {
                                                Toast.makeText(this, "The value in "+fields.getFieldName()+" is less then maximum value", Toast.LENGTH_SHORT).show();
                                                isTrue = false;
                                                break;
                                            }else {
                                                isTrue=true;
                                            }
                                        }
                                    }
                                    if(!isTrue)
                                        break;
                                } else if (viewArrayListComposite.get(j) instanceof Spinner) {
                                    Spinner spinner = (Spinner) viewArrayListComposite.get(j);
                                    int value =  spinner.getSelectedItemPosition();
                                    if(spinner.getTag().equals(fields.getFieldName())) {
                                        if (value==0 ) {
                                            Toast.makeText(this,"Please select data in "+fields.getFieldName(),Toast.LENGTH_SHORT).show();
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

    public void setLabelComposite(String labelName,LinearLayout linearLayout){
        TextView txtView = new TextView(this);
        txtView.setText(labelName.substring(0,1).toUpperCase()+labelName.substring(1)+":");
        txtView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtView.setTextSize(18);
        txtView.setLayoutParams(layoutParams);
        linearLayout.addView(txtView);
    }


    public void setEditTextComposite(Fields fields,LinearLayout linearLayout){

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

        viewArrayListComposite.add(editText);
        linearLayout.addView(editText);
    }


    public void setSpinnerComposite(Fields fields,LinearLayout linearLayout){
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

        viewArrayListComposite.add(spinner);

        linearLayout1.addView(spinner);
        linearLayout.addView(linearLayout1);

    }


    public void setComposite(Fields fields) {
        LinearLayout linearLayout1=new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams1 =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout1.setLayoutParams(layoutParams1);
        linearLayout1.setTag(fields.getFieldName());
        linearLayout1.setOrientation(LinearLayout.VERTICAL);


        if(fields.getFields().size()!=0){
            for (Fields f: fields.getFields()) {
                if (f.getType().equals("text")) {
                    setLabelComposite(f.getFieldName(),linearLayout1);
                    setEditTextComposite(f,linearLayout1);
                } else if (f.getType().equals("number")) {
                    //create a button similarly as above,and add it to the layout
                    setLabelComposite(f.getFieldName(),linearLayout1);
                    setEditTextComposite(f,linearLayout1);
                }else if (f.getType().equals("dropdown")) {
                    setLabelComposite(f.getFieldName(),linearLayout1);
                    setSpinnerComposite(f,linearLayout1);
                    //create a button similarly as above,and add it to the layout
                }else if (f.getType().equals("multiline")) {
                    setLabelComposite(f.getFieldName(),linearLayout1);
                    setEditTextComposite(f,linearLayout1);
                    //create a button similarly as above,and add it to the layout
                }
            }

        }

        viewArrayList.add(linearLayout1);
        linearLayout.addView(linearLayout1);



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



}
