package com.example.countsteps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class InformationsOfUser extends AppCompatActivity {

    TextInputLayout namelo,genderlo,datelo,weightlo,heightlo;
    TextInputEditText nameip,dateip,weightip,heightip;
    TextView genderip;
    Button conbut;
    String selectgender = "Male";

    int age,bornedyear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations_of_user);
        namelo = (TextInputLayout)findViewById(R.id.name_text_input);
        genderlo = (TextInputLayout)findViewById(R.id.gender_text_input);
        datelo = (TextInputLayout)findViewById(R.id.date_text_input);
        weightlo = (TextInputLayout)findViewById(R.id.weight_text_input);
        heightlo = (TextInputLayout)findViewById(R.id.height_text_input);
        nameip = (TextInputEditText)findViewById(R.id.name);
        genderip = (TextView)findViewById(R.id.gender);
        dateip = (TextInputEditText)findViewById(R.id.date);
        weightip = (TextInputEditText)findViewById(R.id.Weight);
        heightip = (TextInputEditText)findViewById(R.id.height);
        conbut = (Button)findViewById(R.id.button);

        genderip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showgenderoption();
            }

            private void showgenderoption() {
                final String[] genders = {"Male","Female"};
                AlertDialog.Builder builder = new AlertDialog.Builder(InformationsOfUser.this);
                builder.setTitle("Gender");
                builder.setSingleChoiceItems(genders, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectgender = genders[which];
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        genderip.setText(selectgender);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        dateip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int selectedYear = calendar.get(Calendar.YEAR);
                int selectedMonth = calendar.get(Calendar.MONTH);
                int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateip.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                        age = selectedYear - year;
                        bornedyear = year;
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(InformationsOfUser.this,android.R.style.Theme_Holo_Light_Dialog,dateSetListener,selectedYear,selectedMonth,selectedDay);
                datePickerDialog.setTitle("Date of Birth");
                datePickerDialog.show();
            }
        });
        conbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bornedyear > Calendar.getInstance().get(Calendar.YEAR)) {
                    Toast.makeText(InformationsOfUser.this,"You can't be born in the future.\nBRUHHHH!!! ",Toast.LENGTH_LONG).show();
                } else if (nameip.getText().toString().trim().length()==0){
                    Toast.makeText(InformationsOfUser.this,"Type your name please!!!",Toast.LENGTH_LONG).show();
                } else if(genderip.getText().toString().trim().length()==0){
                    Toast.makeText(InformationsOfUser.this,"Choose your gender please!!!",Toast.LENGTH_LONG).show();
                } else if(dateip.getText().toString().trim().length()==0){
                    Toast.makeText(InformationsOfUser.this,"Choose your date of birth please!!!",Toast.LENGTH_LONG).show();
                } else if(weightip.getText().toString().trim().length()==0){
                    Toast.makeText(InformationsOfUser.this,"Type your weight please!!!",Toast.LENGTH_LONG).show();
                } else if(heightip.getText().toString().trim().length()==0){
                    Toast.makeText(InformationsOfUser.this,"Type your height please!!!",Toast.LENGTH_LONG).show();
                } else{
                    Intent i1 = new Intent(InformationsOfUser.this, MainActivity.class);
                    i1.putExtra("name",nameip.getText().toString());
                    i1.putExtra("gender",genderip.getText().toString());
                    i1.putExtra("date",dateip.getText().toString());
                    i1.putExtra("weight",weightip.getText().toString());
                    i1.putExtra("height",heightip.getText().toString());
                    i1.putExtra("yearold",Integer.toString(age));
                    startActivity(i1);
                }
            }
        });
    }
}