package com.frestoinc.maildemo.ui.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frestoinc.maildemo.api.listener.MainRowListener;
import com.frestoinc.maildemo.base.BaseViewHolder;
import com.frestoinc.maildemo.data.model.GalContact;
import com.frestoinc.maildemo.databinding.ViewholderContactcardsBinding;
import com.frestoinc.maildemo.utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

/**
 * Created by frestoinc on 26,December,2019 for MailDemo.
 */
public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.AddressBookViewHolder> {

    @Inject
    public MainRowListener listener;
    private List<GalContact> source;
    private AtomicReference<AddressBookActivity> activity = new AtomicReference<>();

    @Inject
    public AddressBookAdapter(AddressBookActivity activity) {
        this.activity.set(activity);
        this.source = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    @NonNull
    @Override
    public AddressBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressBookViewHolder(
                ViewholderContactcardsBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressBookViewHolder holder, int position) {
        holder.bind(position);
    }

    /**
     * Sets source from calling activity.
     *
     * @param list the source
     */
    public void setSource(List<GalContact> list) {
        this.source.clear();
        this.source.addAll(list);
        notifyDataSetChanged();
    }

    protected class AddressBookViewHolder extends BaseViewHolder implements
            View.OnClickListener {

        private ViewholderContactcardsBinding binder;

        AddressBookViewHolder(ViewholderContactcardsBinding binding) {
            super(binding.getRoot());
            this.binder = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void bind(int position) {
            GalContact g = source.get(position);
            if (g != null && binder != null) {
                String username = g.getDisplayName();
                binder.contactDisplayName.setText(username);
                binder.contactDisplayEmail.setText(g.getEmailAddress());
                binder.contactImage.setImageDrawable(Utils.getDrawable(activity.get()));
                binder.contactText.setText(Utils.parseUsername(username));
            }
        }

        @Override
        public void onClick(View v) {
            activity.get().getViewModel().insertContacts(source.get(getAdapterPosition()));
        }
    }
}
