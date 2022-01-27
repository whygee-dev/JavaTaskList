package com.whygee.tasklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button btnAdd, btnReset;
    RecyclerView recyclerView;

    List<MainData> dataList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    Database database;

    MainAdapter mainAdapter;

    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        btnAdd = findViewById(R.id.btn_add);
        btnReset = findViewById(R.id.btn_reset);
        recyclerView = findViewById(R.id.recycler_view);

        database = Database.getInstance(this);

        dataList = database.mainDao().getAll();

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        mainAdapter = new MainAdapter(dataList, MainActivity.this);

        recyclerView.setAdapter(mainAdapter);

        btnAdd.setOnClickListener(v -> {

            String sText = editText.getText().toString().trim();
            if (!sText.equals("")) {

                MainData data = new MainData();
                data.setText(sText);
                database.mainDao().insert(data);

                editText.setText("");
                dataList.clear();
                Toast.makeText(MainActivity.this, "Ajouté avec succès", Toast.LENGTH_LONG).show();

                dataList.addAll(database.mainDao().getAll());
                mainAdapter.notifyDataSetChanged();

            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Veuillez remplir le champ")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle("Action invalide");
                alert.show();
            }
        });
        btnReset.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Etes-vous sûr de vouloir supprimer toutes vos tâches?")
                    .setCancelable(true)
                    .setPositiveButton("Oui", (dialog, id) -> {
                        database.mainDao().reset(dataList);
                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        mainAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Non", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle("Réinitialiser");
            alert.show();
        });
    }
}