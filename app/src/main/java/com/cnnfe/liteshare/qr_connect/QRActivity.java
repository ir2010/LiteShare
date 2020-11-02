package com.cnnfe.liteshare.qr_connect;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cnnfe.liteshare.R;
import com.cnnfe.liteshare.connect.DeviceDetailsFragment;
import com.cnnfe.liteshare.connect.DevicesActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static com.cnnfe.liteshare.connect.DeviceDetailsFragment.msg;


public class QRActivity extends AppCompatActivity {


    private ImageView barcode;
    Button Complete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        barcode = findViewById(R.id.bar_code);
        //textView = findViewById(R.id.data_text);
        //GET THE STRING VALUE
        //String data_in_code = DeviceDetailsFragment.MACAddress; //this string value is being passed in qr
        String data_in_code = DeviceDetailsFragment.name;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode.setImageBitmap(bitmap); //barcode will appear in image view
        } catch (Exception e) {
            e.printStackTrace();
        }

        Complete_btn = findViewById(R.id.button_completion);
        Complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent myIntent = new Intent(getBaseContext(),   FileTransferService.class);
                startActivity(myIntent);*/

                /*if(DevicesActivity.stringUriList.size() != 0 || msg != "")
                {
                    //Uri uri = Uri.parse(DevicesActivity.uriString);
                    new DeviceDetailsFragment().sendFile(DevicesActivity.stringUriList, msg);
                }*/
                Intent intent= new Intent();
               // intent.putStringArrayListExtra("success", uriList);
                //Toast.makeText(AppShow.this, "done", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}