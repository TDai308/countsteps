package com.example.countsteps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.helpers.ChainReference;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TooManyListenersException;

import static com.example.countsteps.R.drawable.refreshing;
import static com.example.countsteps.R.drawable.walking;

public class MainActivity extends AppCompatActivity {
    double distance;
    int stepCount = 0;
    int systemStepCount = 0;
    int tempstep = 0;
    int add = 0;
    boolean[] walkingboolean = {false};
    int progressbar;

    TextView tvstep,tvdistance,tvcalories;
    ProgressBar probar;
    Chronometer timechro;
    Button btstart,btpause,btrefreshing,userbt;
    String name,gender,date,height,weight;

    int age;
    double BMR,speed,METs,calories;

    private double getMETs(double speed) {
        if (speed == 0) {
            return 0;
        } else if (speed> 0 && speed < 3.2) {
            return 2.0;
        } else if (speed == 3.2) {
            return 2.8;
        } else if (speed > 3.2 && speed<= 4.0) {
            return 3.0;
        } else if (speed > 4.0 && speed<= 4.8) {
            return 3.5;
        } else if (speed > 4.8 && speed<= 5.6) {
            return 4.3;
        }
        return 0;
    }

    private double getdistance (int stepCount, int height) {
        return (Math.round((stepCount * (0.45 * height / 100000)) * 100.0) / 100.0);
    }

    private double getBMR(String gender,int weight,int height,int age) {
        if (gender.equals("Male")==true){
            return  ((9.99 * weight) + (6.25 * height) - (4.92 * age) -5);
        } else {
            return  ((9.99 * weight) + (6.25 * height) - (4.92 * age) - 161);
        }
    }

    private double getspeed(double distance, long timeMillis) {
        return (distance / (timeMillis * 2.778 * Math.pow(10, -7)));
    }

    private double getCalories(double BMR ,double METs,long elapsedMillis) {
        return (Math.round((BMR * METs / 24 * (elapsedMillis * 2.778 * Math.pow(10, -7))) * 100.0) / 100.0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        probar = (ProgressBar)findViewById(R.id.progress_circular);
        tvstep= (TextView)findViewById(R.id.countstep);
        tvdistance = (TextView)findViewById(R.id.distanceif);
        tvcalories = (TextView)findViewById(R.id.caloriesif);
        btstart = (Button)findViewById(R.id.play);
        btpause = (Button)findViewById(R.id.pause);
        btrefreshing = (Button)findViewById(R.id.refreshing);
        userbt = (Button)findViewById(R.id.userinfor);
        timechro = (Chronometer)findViewById(R.id.timechro);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        gender = i.getStringExtra("gender");
        date = i.getStringExtra("date");
        weight = i.getStringExtra("weight");
        height = i.getStringExtra("height");
        age = Integer.parseInt(i.getStringExtra("yearold"));

        final SensorManager ss = (SensorManager)getSystemService(SENSOR_SERVICE);
        final Sensor countstep = ss.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        BMR = getBMR(gender,Integer.parseInt(weight),Integer.parseInt(height),age);

        if(countstep == null){
            Toast.makeText(this,"KHONG CO SENSOR", Toast.LENGTH_SHORT).show();
        }
        else{
            timechro.setBase(SystemClock.elapsedRealtime());
            final SensorEventListener ssListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                        int step = (int) event.values[0];
                        if (systemStepCount == 0) {
                            systemStepCount = step;
                        } else {
                            add = step - systemStepCount - tempstep;
                            stepCount += add;
                            tempstep = step - systemStepCount;
                        }
                        progressbar = (stepCount * 100) / 1000;
                        probar.setProgress(progressbar);
                        distance = getdistance(stepCount,(Integer.parseInt(height)));
                        long elapsedMillis = SystemClock.elapsedRealtime() - timechro.getBase();
                        speed = getspeed(distance,elapsedMillis);
                        METs = getMETs(speed);
                        calories = getCalories(BMR,METs,elapsedMillis);
                        tvcalories.setText("" + calories + "kcal");
                        tvstep.setText("" + stepCount);
                        tvdistance.setText("" + distance + "km");
                    }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

            btstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (walkingboolean[0]  == false){
                        timechro.setBase(SystemClock.elapsedRealtime());
                        timechro.start();
                        walkingboolean[0]  = true;
                    }
                    ss.registerListener(ssListener, countstep, SensorManager.SENSOR_DELAY_FASTEST);
                    btstart.setEnabled(false);
                }
            });

            btpause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"Touch and hold to end",Toast.LENGTH_LONG).show();
                }
            });

            btpause.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (walkingboolean[0]  == true){
                        timechro.stop();
                        walkingboolean[0]  = false;
                    }
                    btstart.setEnabled(false);
                    ss.unregisterListener(ssListener, countstep);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Congrantulation!!!");
                    builder.setIcon(R.drawable.fireworks);
                    builder.setMessage("You have finished "+ stepCount +" steps");
                    builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            btrefreshing.setEnabled(true);
                            btrefreshing.setBackgroundResource(refreshing);
                        }
                    });
                    builder.show();
                    btpause.setEnabled(false);
                    return false;
                }
            });
        }

        btrefreshing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(MainActivity.this, InformationsOfUser.class);
                startActivity(i1);
            }
        });

        userbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("User information");
                builder.setIcon(R.drawable.nguoikhoemanh);
                builder.setMessage("Name: " + name +"\nGender: "+ gender+ "\nAge: "+ age + "\nWeight: "+weight+ " Kg\nHeight: "+ height + " Cm");
                builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
}