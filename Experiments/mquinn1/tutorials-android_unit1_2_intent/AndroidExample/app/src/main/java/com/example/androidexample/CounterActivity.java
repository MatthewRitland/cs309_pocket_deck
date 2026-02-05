package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt;
    private TextView msg; // define number textview variable
    private Button increaseBtn; // define increase button variable
    private Button decreaseBtn; // define decrease button variable
    private Button backBtn;     // define back button variable

    private int counter = 2;    // counter variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        /* initialize UI elements */
        msg = findViewById(R.id.msg);
        numberTxt = findViewById(R.id.number);
        increaseBtn = findViewById(R.id.counter_increase_btn);
        decreaseBtn = findViewById(R.id.counter_decrease_btn);
        backBtn = findViewById(R.id.counter_back_btn);

        /* when increase btn is pressed, counter++, reset number textview */
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    counter = Math.multiplyExact(counter, counter);
                    numberTxt.setText(String.valueOf(counter));
                }catch (ArithmeticException e){
                    msg.setText("Overflow");
                }
            }
        });

        /* when decrease btn is pressed, counter--, reset number textview */
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.setText("");
                counter = (int) Math.sqrt(counter);
                numberTxt.setText(String.valueOf(counter));

                if(counter == 1){
                    counter = 2;
                    numberTxt.setText(String.valueOf(counter));
                }
            }
        });

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("NUM", String.valueOf(counter));  // key-value to pass to the MainActivity
                startActivity(intent);


                
            }
        });

    }
}