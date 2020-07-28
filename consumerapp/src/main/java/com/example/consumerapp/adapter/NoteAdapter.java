package com.example.consumerapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumerapp.CustomOnItemClickListener;
import com.example.consumerapp.NoteAddUpdateActivity;
import com.example.consumerapp.R;
import com.example.consumerapp.entity.Note;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private ArrayList<Note> noteArrayList = new ArrayList<>();
    private Activity activity;

    public NoteAdapter(Activity activity){
        this.activity = activity;
    }

    public ArrayList<Note> getListNote(){
        return noteArrayList;
    }

    public void setListNote(ArrayList<Note> listNote){
        if (noteArrayList.size() > 0){
            this.noteArrayList.clear();
        }
        this.noteArrayList.addAll(listNote);
        notifyDataSetChanged();
    }

    public void addItem(Note note){
        this.noteArrayList.add(note);
        notifyItemInserted(noteArrayList.size() - 1);
    }

    public void updateItem(int position, Note note){
        this.noteArrayList.set(position, note);
        notifyItemChanged(position, note);
    }

    public void removeItem(int position){
        this.noteArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, noteArrayList.size());
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteViewHolder holder, int position) {
        holder.tvTitle.setText(noteArrayList.get(position).getTitle());
        holder.tvDescription.setText(noteArrayList.get(position).getDescription());
        holder.tvDate.setText(noteArrayList.get(position).getDate());
        holder.cvNote.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClickCallback(View view, int position) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, noteArrayList.get(position));
                activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDescription, tvDate;
        final CardView cvNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            cvNote = itemView.findViewById(R.id.cv_note);
        }
    }
}
