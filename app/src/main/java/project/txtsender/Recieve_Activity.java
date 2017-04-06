package project.txtsender;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Recieve_Activity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    EditText editTextAddress,editTextFileName;
    Button buttonConnect;
    TextView textPort;
    static final int SocketServerPORT = 8080;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieve_);


        editTextAddress = (EditText) findViewById(R.id.address);
        editTextFileName = (EditText) findViewById(R.id.filename);

        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.connect);

        buttonConnect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ClientRxThread clientRxThread =
                        new ClientRxThread(
                                editTextAddress.getText().toString(),editTextFileName.getText().toString(),
                                SocketServerPORT);
                clientRxThread.start();
            }});

    }
    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;
        String fileName;

        ClientRxThread(String address,String FileName, int port) {
            dstAddress = address;
            dstPort = port;
            fileName = FileName;
            //Toast.makeText(receiveActivity.this, dstAddress +"\n"+fileName, Toast.LENGTH_LONG).show();
        }

        @Override
        public void run() {
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                //Toast.makeText(receiveActivity.this, dstAddress+"\n"+fileName, Toast.LENGTH_LONG).show();
                File file = new File(
                        Environment.getExternalStorageDirectory(),
                        "txtsender/"+fileName);

                byte[] bytes = new byte[4096];
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int bytesRead = is.read(bytes, 0, bytes.length);
                bos.write(bytes, 0, bytesRead);
                bos.close();
                socket.close();
                Recieve_Activity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Recieve_Activity.this,
                                "Finished",
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (IOException e) {

                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();
                Recieve_Activity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Recieve_Activity.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void QrScanner(View view){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        String[] fromScanner=rawResult.getText().toString().split("::");
        //Toast.makeText(receiveActivity.this, fromScanner[0]+"\n"+fromScanner[1], Toast.LENGTH_LONG).show();
        editTextAddress.setText(fromScanner[0]);
        editTextFileName.setText(fromScanner[1]);
        buttonConnect.performClick();
    }
    @Override
    public void onBackPressed() {
        this.finish();
        return;
    }
}
