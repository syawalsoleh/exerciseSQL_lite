package com.example.exercisesql_lite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ModKontak > newsList = new ArrayList <ModKontak>();
    private ListKontakAdapter adapter;
    ListKontakAdapter custom = new ListKontakAdapter(null, newsList);
    ListView listView;
    public String[] idUserArray;


    private long lastPressedTime;
    private static final int PERIOD = 2000;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {

                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "Tekan 2 kali untuk keluar",Toast.LENGTH_SHORT).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= (ListView)findViewById(R.id.listView);
        adapter = new ListKontakAdapter(MainActivity.this, newsList);
        listView.setAdapter(adapter);
        reloadData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                Intent i = new Intent(getApplicationContext(),clickList.class);
                i.putExtra("mode", "Edit");
                i.putExtra("id_user",idUserArray[position]);
                startActivity(i);
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView   <? > parent, View view, final int position, long id) {

                    PopupMenu popup = new PopupMenu(MainActivity.this, view);
                    popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            if(item.getTitle().equals("Edit Data")){

                                Intent i = new Intent(getApplicationContext(),update.class);
                                i.putExtra("mode", "Edit");
                                i.putExtra("id_user",idUserArray[position]);
                                startActivity(i);
                                finish();


                            }else{

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle("Hapus Data");
                                alertDialog.setMessage("Anda Yakin Ingin Menghapus Data ini?");
                                alertDialog.setPositiveButton("Hapus",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                DBHelper db = new DBHelper(getApplicationContext());
                                                int status = db.HapusData(idUserArray[position]);
                                                if(status >0){
                                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                                    finish();
                                                    overridePendingTransition(-1, -1);
                                                    startActivity(i);
                                                    overridePendingTransition(-1, -1);
                                                    Toast.makeText(MainActivity.this, "Data Berhasil Dihapus ",Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(MainActivity.this, "Data Gagal Dihapus ",Toast.LENGTH_LONG).show();
                                                }
                                                newsList.clear();
                                                reloadData();

                                            }
                                        });

                                alertDialog.setNegativeButton("Tidak",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                            }
                                        });
                                alertDialog.show();
                            }
                            return true;
                        }});popup.show();

                        return true;
            }
        });
    }
    public View Tambah(View v){
        Intent i = new Intent(getApplicationContext(),CRUDkontak.class);
        i.putExtra("mode", "Tambah");
        startActivity(i);
        finish();
        return v;
    }

    private void reloadData(){
        DBHelper db = new DBHelper(getApplicationContext());
        Cursor cursor=db.AmbilSemuaData();

        if (cursor.moveToFirst()) {

            idUserArray=new String[cursor.getCount()];
            int i=0;
            newsList.clear();
            do {
                ModKontak modKontak = new ModKontak();

                modKontak.setIdUser(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                modKontak.setNama(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAMA)));
                modKontak.setPhone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE)));
                modKontak.setEmail(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL)));
                modKontak.setAlamat(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ALAMAT)));

                idUserArray[i] = modKontak.getIdUser();

                i++;
                newsList.add(modKontak);
            } while (cursor.moveToNext());

            // Tutup Koneksi
            cursor.close();
            db.close();
        }
    }
}
