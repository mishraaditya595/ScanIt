package com.vob.scanner.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.vob.scanner.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import static com.vob.scanner.constants.Constants.COLOR_PDF;
import static com.vob.scanner.constants.Constants.FOLDER;

public class DisplayActivity extends AppCompatActivity {

    String filename = "";

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
                getFileName();
            }
        });
    }

    private void exportPDF() {
        FileOutputStream outputStream = null;
        File storage = Environment.getExternalStorageDirectory();
        File directory = new File(storage.getAbsolutePath()+"/ScanIt/Export/pdf");
        directory.mkdirs();

        File export = new File(directory,filename);

        try {
            outputStream = new FileOutputStream(export);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document document = new Document();
        String directoryPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                                            +"/ScanIt/Export/pdf";

        try {
            PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/" + filename + ".pdf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.open();

        Image image = null;
        try {
            image = Image.getInstance(directoryPath + "/" + filename + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        }

        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0)
                                             / image.getWidth()) * 100;
        image.scalePercent(scaler);
        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

        try {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();

        Toast.makeText(this, "PDF Saved", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void getFileName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save as");
        builder.setMessage("Enter a name for your files");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename = input.getText().toString();
                Toast.makeText(DisplayActivity.this, filename, Toast.LENGTH_SHORT).show();
                exportPDF();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private PdfDocument bitmapToPDF(Bitmap bitmap)  {
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
        return pdfDocument;
    }

    private void saveFile(PdfDocument pdfDocument) {
        File root = new File(getCacheDir(), "ScanIt");

        File file = new File(root, filename);

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
                Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}