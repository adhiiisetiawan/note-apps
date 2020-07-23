package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notesapp.db.NoteHelper;
import com.example.notesapp.entity.Note;

import com.example.notesapp.db.DatabaseContract.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.provider.CallLog.Calls.DATE;
import static android.provider.ContactsContract.CommonDataKinds.Organization.TITLE;
import static android.provider.MediaStore.Video.VideoColumns.DESCRIPTION;


public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtTitle, edtDescription;
    private Button btnSubmit;

    private boolean isEdit;

    private Note note;
    private int position;
    private NoteHelper noteHelper;

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edtTitle = findViewById(R.id.edt_text_title);
        edtDescription = findViewById(R.id.edt_text_description);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(this);

        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.open();

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null){
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            note = new Note();
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit){
            actionBarTitle = "Change";
            btnTitle = "Update";

            if (note != null){
                edtTitle.setText(note.getTitle());
                edtDescription.setText(note.getDescription());
            }
        } else {
            actionBarTitle = "Add";
            btnTitle = "Save";
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnSubmit.setText(btnTitle);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit){
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)){
                edtTitle.setError("Field can not be blank");
                return;
            }

            note.setTitle(title);
            note.setDescription(description);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, note);
            intent.putExtra(EXTRA_POSITION, position);

            ContentValues values = new ContentValues();
            values.put(TITLE, title); //tanda tanya
            values.put(DESCRIPTION, description); //tanda tanya

            if (isEdit){
                long result = noteHelper.update(String.valueOf(note.getId()), values);
                if (result > 0){
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                note.setDate(getCurrentDate());
                values.put(DATE, getCurrentDate());
                long result = noteHelper.insert(values);
                if (result > 0) {
                    note.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    finish();
                } else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose){
            dialogTitle = "Cancel";
            dialogMessage = "Are you sure to cancel?";
        } else {
            dialogMessage = "Are you sure to delete this item?";
            dialogTitle = "Delete Note";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose){
                            finish();
                        } else {
                            long result = noteHelper.deleteById(String.valueOf(note.getId()));
                            if (result > 0){
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, intent);
                                finish();
                            } else {
                                Toast.makeText(NoteAddUpdateActivity.this, "Failed delete data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}