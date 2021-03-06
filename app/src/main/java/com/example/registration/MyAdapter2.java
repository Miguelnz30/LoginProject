package com.example.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    private AdminActivity activity;
    private List<Model> mList;
    private List<Model> mListOriginal;
    Model meta;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyAdapter2(AdminActivity activity, List<Model> mList) {
        this.activity = activity;
        this.mList = mList;


        mListOriginal = new ArrayList<>();
        mListOriginal.addAll(mList);
    }

    public void filtrado(final String txtBuscar){
        int logitud = txtBuscar.length();
        if(logitud == 0){
            mList.clear();
            mList.addAll(mListOriginal);
            activity.showData();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Model> collecion = mList.stream().filter(i -> i.getTitle().toLowerCase().contains(txtBuscar.toLowerCase())
                        ||i.getTime().toLowerCase().contains(txtBuscar.toLowerCase())
                        ||i.getDate().toLowerCase().contains(txtBuscar.toLowerCase())
                        ||i.getDesc().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                mList.clear();
                mList.addAll(collecion);
            } else {
                for (Model m: mListOriginal) {
                    if(m.getTitle().toLowerCase().contains(txtBuscar.toLowerCase())
                            ||m.getDesc().toLowerCase().contains(txtBuscar.toLowerCase())
                            ||m.getDate().toLowerCase().contains(txtBuscar.toLowerCase())
                            ||m.getDesc().toLowerCase().contains(txtBuscar.toLowerCase())){
                        mList.add(m);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void updateData(int position) {
        Model item = mList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uId", item.getId());
        bundle.putString("uTitle", item.getTitle());
        bundle.putString("uDesc", item.getDesc());
        bundle.putString("uDate", item.getDate());
        bundle.putString("uTime", item.getTime());
        Intent intent = new Intent(activity, MainActivity2.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void deleteData(int position){
        Model item = mList.get(position);
        db.collection("Documents").document(item.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            notifyRemoved(position);
                            Toast.makeText(activity, "Datos Eliminados!", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(activity, "Error"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void notifyRemoved(int position){
        mList.remove(position);
        notifyItemRemoved(position);
        activity.showData();
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public MyAdapter2.MyViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item , parent , false);
        return new MyAdapter2.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.title.setText(mList.get(position).getTitle());
        holder.desc.setText(mList.get(position).getDesc());
        holder.date.setText(mList.get(position).getDate());
        holder.time.setText(mList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, desc, date, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_text);
            desc = itemView.findViewById(R.id.desc_text);
            date = itemView.findViewById(R.id.date_text);
            time = itemView.findViewById(R.id.time_text);
        }
    }
}
