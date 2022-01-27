package com.whygee.tasklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private final List<MainData> dataList;
    private final Activity context;
    private Database database;

    AlertDialog.Builder builder;

    public MainAdapter(List<MainData> dataList, Activity context) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        MainData data = dataList.get(position);
        database = Database.getInstance(context);
        holder.textView.setText(data.getText());

        holder.btnEdit.setOnClickListener(v -> {
            MainData d = dataList.get(holder.getAdapterPosition());
            int sID = d.getID();
            String sText = d.getText();
            Dialog dialog = new Dialog(context);

            dialog.setContentView(R.layout.dialog_update);

            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);

            dialog.show();
            EditText editText = dialog.findViewById(R.id.edit_text);
            Button btUpdate = dialog.findViewById(R.id.btn_update);

            editText.setText(sText);

            btUpdate.setOnClickListener(v1 -> {
                dialog.dismiss();
                String uText = editText.getText().toString().trim();
                database.mainDao().upate(sID, uText);
                dataList.clear();
                dataList.addAll(database.mainDao().getAll());
                notifyDataSetChanged();
            });


        });

        holder.btnDelete.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Etes-vous sûr de vouloir supprimer cette tâche?")
                    .setCancelable(true)
                    .setPositiveButton("Oui", (dialog, id) -> {
                        MainData d = dataList.get(holder.getAdapterPosition());

                        database.mainDao().delete(d);
                        int position1 = holder.getAdapterPosition();
                        dataList.remove(position1);
                        notifyItemRemoved(position1);
                        notifyItemRangeChanged(position1, dataList.size());
                    })
                    .setNegativeButton("Non", (dialog, id) -> {
                        dialog.cancel();
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Confirmation de la suppression");
            alert.show();

        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
