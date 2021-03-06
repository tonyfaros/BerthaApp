package com.example.BerthaApp.RegisterCar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.BerthaApp.Pattern.Singleton;
import com.example.BerthaApp.Profile.My_Cars;
import com.example.BerthaApp.Profile.ProfileActivity;
import com.example.BerthaApp.R;
import com.example.BerthaApp.StartDrive.Main4Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "Register car";

    private Spinner spinner_fuel, spinner_transmission, spinner_car_make, spinner_car_model, spinner_car_year,
            spinner_car_engine, spinner_drive_cond;

    private EditText licence_plate, tire_size;

    private final static String url_make = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getMakes";

    private final static String url_modelBy_make = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getModels&make=";

    private final static String url_model_trim = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&make=" ;

    private final static String url_trim_specs = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getModel&model=";

    private final static String[] year = new String[] {"Choose year", "2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010"
            ,"2011","2012","2013","2014","2015","2016","2017","2018","2019"};

    private final static String[] fuel_type = new String[] {"Gasoline", "Diesel"};

    private final static String[] transmission_type = new String[] {"Automatic", "Manual"};

    private final static String [] drive_cond = new String[] { "Urban","Rural"};



    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String id_user = "id_user";
    private String id_userLogged;

    private Button save_carButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        id_userLogged = sharedPreferences.getString(id_user,"");

        spinner_fuel = findViewById(R.id.spinner_fuel);
        spinner_transmission= findViewById(R.id.spinner_transmission);
        spinner_car_make = findViewById(R.id.spinner_make);
        spinner_car_model = findViewById(R.id.spinner_model);
        spinner_car_year = findViewById(R.id.spinner_year);
        spinner_car_engine = findViewById(R.id.spinner_engine);
        spinner_drive_cond = findViewById(R.id.spinner_driveCond);

        licence_plate = findViewById(R.id.editText_plate);
        tire_size = findViewById(R.id.editText_tireSize);

        save_carButton = findViewById(R.id.save_carButton);

        save_carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_fields()){
                    Toast.makeText(getApplicationContext(), "Vehiculo registrado", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), "Por favor completa al menos marca, modelo, año y motor", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getAllMake();

        ArrayAdapter<String> spinner_fuelAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,fuel_type);
        spinner_fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fuel.setAdapter(spinner_fuelAdapter);

        final ArrayAdapter<String> spinner_engine = new ArrayAdapter<>(this,R.layout.spinner_item,list_engineMod);
        spinner_engine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_engine.setAdapter(spinner_engine);

        ArrayAdapter<String> spinner_transmissionAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,transmission_type);
        spinner_transmissionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_transmission.setAdapter(spinner_transmissionAdapter);

        ArrayAdapter<String> spinner_year = new ArrayAdapter<>(this,R.layout.spinner_item,year);
        spinner_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_year.setAdapter(spinner_year);

        ArrayAdapter<String> spinner_condDrive = new ArrayAdapter<>(this,R.layout.spinner_item,drive_cond);
        spinner_condDrive.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_drive_cond.setAdapter(spinner_condDrive);

        ArrayAdapter<String> spinner_modelAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,list_models);
        spinner_modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_model.setAdapter(spinner_modelAdapter);

        ArrayAdapter<String> spinner_makeAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,listaMarcas);
        spinner_makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_make.setAdapter(spinner_makeAdapter);

        spinner_car_make.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Choose make")){}else{
                    getModelByMake(spinner_car_make.getSelectedItem().toString());
                    list_engineMod = new ArrayList<>(Arrays.asList("Choose engine"));
                    spinner_car_engine.setAdapter(spinner_engine);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_car_engine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Choose engine")){}
                else{
                    String id_selected = list_modelId.get(position);
                    getTrimsSpecs(id_selected);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_car_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!spinner_car_model.getSelectedItem().toString().equals("Choose model") && !spinner_car_make.getSelectedItem().toString().equals("Choose make")){

                    getEngine(spinner_car_make.getSelectedItem().toString(), spinner_car_model.getSelectedItem().toString(), spinner_car_year.getSelectedItem().toString());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_green));
    }

    ArrayList<String> list_engineMod = new ArrayList<>(Arrays.asList("Choose engine"));

    ArrayList<String> list_modelId = new ArrayList<>();
    public void getEngine(String make, String model, String year){



        list_engineMod.clear();
        list_modelId.clear();

        if(make.contains(" ") || model.contains(" ")){
            make = make.replaceAll("\\s","-");
            model = model.replaceAll("\\s","-");

        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_model_trim + make + "&year=" + year + "&model=" + model,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        String json = response.replaceAll("[(?);]","");


                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray jsonArray = jsonObject.getJSONArray("Trims");

                            for (int i=0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    // Pulling items from the array
                                    String model_trim = oneObject.getString("model_trim");
                                    String model_id =oneObject.getString("model_id");

                                    list_modelId.add(model_id);
                                    list_engineMod.add(model_trim);


                                } catch (JSONException e) {
                                    // Oops
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(list_engineMod.isEmpty()){
                            Toast.makeText(Main3Activity.this, "No se encontraron modelos de motor", Toast.LENGTH_SHORT).show();
                            list_engineMod = new ArrayList<>(Arrays.asList("Choose engine"));
                            ArrayAdapter<String> spinner_engineAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,list_engineMod);
                            spinner_engineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_car_engine.setAdapter(spinner_engineAdapter);
                        }else{
                            ArrayAdapter<String> spinner_engineAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,list_engineMod);
                            spinner_engineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_car_engine.setAdapter(spinner_engineAdapter);
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NOP", Toast.LENGTH_SHORT).show();
            }

        });

// Add the request to the RequestQueue.
        Singleton.getInstance(Main3Activity.this).addToRequestQueue(stringRequest);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
        startActivity(intent);
        finish();
    }

    ArrayList<String> listaMarcas = new ArrayList<String>(Arrays.asList("Choose make"));

    public void getAllMake(){
        listaMarcas.clear();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_make,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        String json = response.replaceAll("[(?);]","");
                        listaMarcas.add("Choose make");
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray jsonArray = jsonObject.getJSONArray("Makes");

                            for (int i=0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    // Pulling items from the array
                                    String oneObjectsItem = oneObject.getString("make_display");

                                    listaMarcas.add(oneObjectsItem);
                                } catch (JSONException e) {
                                    // Oops
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> spinner_makeAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,listaMarcas);
                        spinner_makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_car_make.setAdapter(spinner_makeAdapter);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NOP", Toast.LENGTH_SHORT).show();
            }

        });

// Add the request to the RequestQueue.
        Singleton.getInstance(Main3Activity.this).addToRequestQueue(stringRequest);

    }

    ArrayList<String> list_models = new ArrayList<>(Arrays.asList("Choose model"));

    public void getModelByMake(String make){
        list_models.clear();

        if(make.contains(" ")){
            make = make.replaceAll("\\s","-");

        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_modelBy_make + make,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        String json = response.replaceAll("[(?);]","");
                        list_models.add("Choose model");
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray jsonArray = jsonObject.getJSONArray("Models");

                            for (int i=0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    // Pulling items from the array
                                    String oneObjectsItem = oneObject.getString("model_name");

                                    list_models.add(oneObjectsItem);
                                } catch (JSONException e) {
                                    // Oops
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> spinner_modelAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,list_models);
                        spinner_modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_car_model.setAdapter(spinner_modelAdapter);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NOP", Toast.LENGTH_SHORT).show();
            }

        });

// Add the request to the RequestQueue.
        Singleton.getInstance(Main3Activity.this).addToRequestQueue(stringRequest);

    }



    ArrayList<String> list_trim_specs =  new ArrayList<>();
    public void getTrimsSpecs(String id_model){

        list_trim_specs.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_trim_specs + id_model,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        String json = response.replaceAll("[(?);]","");

                        try {
                            JSONArray jsonArray = new JSONArray(json);

                            for (int i=0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject oneObject = jsonArray.getJSONObject(i);
                                    // Pulling items from the array


                                    String model_weighKg = oneObject.getString("model_weight_kg");
                                    String engine_cc= oneObject.getString("model_engine_cc");
                                    String model_length_mm= oneObject.getString("model_length_mm");
                                    String model_width_mm= oneObject.getString("model_width_mm");
                                    String model_height_mm= oneObject.getString("model_height_mm");
                                    String model_lkm_hwy= oneObject.getString("model_lkm_hwy");
                                    String model_lkm_city= oneObject.getString("model_lkm_city");
                                    String model_mpg_mixed= oneObject.getString("model_lkm_mixed");
                                    String model_body= oneObject.getString("model_body");
                                    String model_doors= oneObject.getString("model_doors");
                                    String model_drive= oneObject.getString("model_drive");
                                    String model_engine_position= oneObject.getString("model_engine_position");
                                    String model_engine_type= oneObject.getString("model_engine_type");


                                    list_trim_specs.add(model_weighKg);
                                    list_trim_specs.add(engine_cc);
                                    list_trim_specs.add(model_length_mm);
                                    list_trim_specs.add(model_width_mm);
                                    list_trim_specs.add(model_height_mm);
                                    list_trim_specs.add(model_lkm_hwy);
                                    list_trim_specs.add(model_lkm_city);
                                    list_trim_specs.add(model_mpg_mixed);
                                    list_trim_specs.add(model_body);
                                    list_trim_specs.add(model_doors);
                                    list_trim_specs.add(model_drive);
                                    list_trim_specs.add(model_engine_position);
                                    list_trim_specs.add(model_engine_type);



                                } catch (JSONException e) {
                                    // Oops
                                }
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NOP", Toast.LENGTH_SHORT).show();
            }

        });

// Add the request to the RequestQueue.
        Singleton.getInstance(Main3Activity.this).addToRequestQueue(stringRequest);

    }





    private boolean check_fields(){

        String[] params = new String[23];

        String car_make = spinner_car_make.getSelectedItem().toString();
        String car_model = spinner_car_model.getSelectedItem().toString();
        String car_year = spinner_car_year.getSelectedItem().toString();
        String car_engine = spinner_car_engine.getSelectedItem().toString();
        String car_drive_cond = spinner_drive_cond.getSelectedItem().toString();
        String car_transmission = spinner_transmission.getSelectedItem().toString();
        String car_fuel = spinner_fuel.getSelectedItem().toString();

        String car_licence = licence_plate.getText().toString();
        String car_tireSize = tire_size.getText().toString();

        if(car_make.equals("Choose make") || car_model.equals("Choose model") || car_year.equals("Choose year") || (car_engine.equals("Choose engine"))){
            return false;
        }

        params[0] = id_userLogged;
        params[1] = car_make;
        params[2] = car_model;
        params[3] = car_year;
        params[4] = car_engine;
        params[5] = car_licence;
        params[6] = car_transmission;
        params[7] = car_fuel;
        params[8] = car_drive_cond;
        params[9] = car_tireSize;


        int paramsCount = 10;
        for(String i : list_trim_specs){
            params[paramsCount] = i;
            paramsCount++;
        }


        postCar(params);

        return true;
    }



    public void postCar(final String[] arg0){

        String url = "https://evening-oasis-22037.herokuapp.com/cars/car_create/";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int id = jsonObject.getInt("@last_id_car");

                            Singleton.getInstance(getApplicationContext()).addList_cars(new My_Cars(id,arg0[1],arg0[2],arg0[4]));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id_user", arg0[0]);
                params.put("make", arg0[1]);
                params.put("model", arg0[2]);
                params.put("year", arg0[3]);
                params.put("model_trim", arg0[4]);
                params.put("license_plate", arg0[5]);
                params.put("car_transmission", arg0[6]);
                params.put("fuel_type", arg0[7]);
                params.put("car_drive_conditions", arg0[8]);
                params.put("car_tireSize", arg0[9]);
                params.put("weigh_kg", arg0[10]);
                params.put("engine_cc", arg0[11]);
                params.put("length_mm", arg0[12]);
                params.put("width_mm", arg0[13]);
                params.put("height_mm", arg0[14]);
                params.put("lkm_hwy", arg0[15]);
                params.put("lkm_city", arg0[16]);
                params.put("lkm_mixed", arg0[17]);
                params.put("body_style", arg0[18]);
                params.put("door_number", arg0[19]);
                params.put("drive", arg0[20]);
                params.put("engine_position", arg0[21]);
                params.put("engine_type", arg0[22]);


                return params;
            }
        };
        Singleton.getInstance(Main3Activity.this).addToRequestQueue(postRequest);
    }
}
