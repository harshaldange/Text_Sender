package project.txtsender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button sen,rec;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sen=(Button)findViewById(R.id.btnSend);
        rec=(Button)findViewById(R.id.btnRec);



        sen.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, sendActivity.class);
//                startActivity(i);
                Intent i = new Intent(MainActivity.this, File_Activity.class);
                startActivity(i);
            }

        });

        rec.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent j = new Intent(MainActivity.this, Recieve_Activity.class);
                startActivity(j);
            }

        });
    }
}
