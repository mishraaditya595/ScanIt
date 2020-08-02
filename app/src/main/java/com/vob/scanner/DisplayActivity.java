package com.vob.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.vob.scanner.constants.Constants.COLOR_PDF;
import static com.vob.scanner.constants.Constants.FOLDER;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        String image = intent.getStringExtra("image");

        assert image != null;
        final Bitmap bitmap = stringToBitmap(image);

        ((ImageView)findViewById(R.id.image_display_iv)).setImageBitmap(bitmap);

        ((Button)findViewById(R.id.convert_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    bitmapToPDF(bitmap);
            }
        });
    }

    private void bitmapToPDF(Bitmap bitmap)  {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(COLOR_PDF));
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);

        pdfDocument.finishPage(page);

        File root = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!root.exists())
        {
            root.mkdir();
        }

        File file = new File(root, "picture.pdf");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            pdfDocument.writeTo(fileOutputStream);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap stringToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT        );
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}