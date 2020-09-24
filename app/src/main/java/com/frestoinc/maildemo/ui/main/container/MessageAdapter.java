package com.frestoinc.maildemo.ui.main.container;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.api.listener.MainRowListener;
import com.frestoinc.maildemo.base.BaseViewHolder;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.databinding.ViewholderInboxBinding;
import com.frestoinc.maildemo.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by frestoinc on 18,December,2019 for MailDemo.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.InboxViewHolder> {

    @Inject
    public MainRowListener listener;
    private List<EasMessage> source;
    private Context ctx;

    @Inject
    public MessageAdapter(Context context) {
        this.ctx = context;
        this.source = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InboxViewHolder(
                ViewholderInboxBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        holder.bind(position);
    }

    /**
     * Sets source from calling activity.
     *
     * @param source the source
     */
    public void setSource(List<EasMessage> source) {
        this.source.clear();
        this.source.addAll(source);
        notifyDataSetChanged();
    }

    protected class InboxViewHolder extends BaseViewHolder implements
            View.OnClickListener {

        private ViewholderInboxBinding binder;

        InboxViewHolder(ViewholderInboxBinding binding) {
            super(binding.getRoot());
            this.binder = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void bind(int position) {
            EasMessage e = source.get(position);

            String username = Utils.parseUsername(e);
            binder.cInboxName.setText(username);
            binder.cInboxText.setText(Utils.parseUsername(username));

            binder.cInboxSubject.setText(
                    Utils.isStringValid(e.getSubject())
                            ? e.getSubject() : "No Subject");

            Utils.parseMessage(e.getMessage(), binder.cInboxMsg);

            binder.cInboxImage.setImageDrawable(Utils.getDrawable(ctx));

            binder.cInboxTime.setText(
                    Utils.isStringValid(e.getDateReceived())
                            ? Utils.convertDateToTimespan(e.getDateReceived()) : "00:00 xx");

            binder.cInboxAttachment.setVisibility(
                    e.hasAttachment() ? View.VISIBLE : View.GONE);
            if (e.hasAttachment()) {
                binder.cInboxAttachment.setImageDrawable(
                        ctx.getDrawable(e.getAttachmentName().endsWith(".p7m")
                                ? R.drawable.ic_lock_red_24dp : R.drawable.ic_attach_file_black_24dp));
            }

            if (e.isRead()) {
                binder.cInboxSubject.setTypeface(binder.cInboxSubject.getTypeface(), Typeface.BOLD);
                binder.cInboxName.setTypeface(binder.cInboxSubject.getTypeface(), Typeface.BOLD);
                binder.cInboxText.setTypeface(binder.cInboxSubject.getTypeface(), Typeface.BOLD);
            }
        }

        @Override
        public void onClick(View v) {
            listener.onMessageRowItemClicked(ctx, source.get(getAdapterPosition()));
        }
    }
}
