package com.moutamid.callmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.moutamid.callmanager.Constants;
import com.moutamid.callmanager.R;
import com.moutamid.callmanager.models.ContactModel;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactVH> {
    Context context;
    ArrayList<ContactModel> list;

    public ContactsAdapter(Context context, ArrayList<ContactModel> list ) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ContactVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactVH(LayoutInflater.from(context).inflate(com.moutamid.callmanager.R.layout.contacts_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactVH holder, int position) {
        ContactModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getContactName() + " (" + model.getContactNumber() +")");
        holder.remove.setOnClickListener(v -> {
            list.remove(model);
            Stash.put(Constants.CONTACTS, list);
            notifyItemRemoved(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContactVH extends RecyclerView.ViewHolder{

        TextView name;
        ImageView remove;

        public ContactVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            remove = itemView.findViewById(R.id.remove);
        }
    }

}
