package com.example.apppmn;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppmn.ItemAdapter;
import com.example.apppmn.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ItemAdapter adapter;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de la base de données
        com.example.myapp.DatabaseHelper databaseHelper = new com.example.myapp.DatabaseHelper(this);
        databaseHelper.checkAndCopyDatabase();
        database = databaseHelper.openDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDatabase(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Charger tous les éléments au démarrage
        searchDatabase("");
    }

    private void searchDatabase(String query) {
        ArrayList<String> results = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Items WHERE name LIKE ?", new String[]{"%" + query + "%"});
        while (cursor.moveToNext()) {
            results.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();

        adapter.updateData(results);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}