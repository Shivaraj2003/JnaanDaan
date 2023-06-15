//package com.example.myapplication;
//
//import android.content.Context;
//import android.provider.ContactsContract;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import java.util.List;
//
//public class NotesAdapter extends ArrayAdapter<ContactsContract.CommonDataKinds.Note> {
//    private Context context;
//    private List<ContactsContract.CommonDataKinds.Note> notesList;
//
//    public NotesAdapter(Context context, List<ContactsContract.CommonDataKinds.Note> notesList) {
//        super(context, 0, notesList);
//        this.context = context;
//        this.notesList = notesList;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null) {
//            view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
//        }
//
//        // Get the current note item
//        ContactsContract.CommonDataKinds.Note note = notesList.get(position);
//
//        // Set the note title
//        TextView titleTextView = view.findViewById(R.id.titleTextView);
//        titleTextView.setText(note.getNotesTitle());
//
//        return view;
//    }
//}
