package com.habitrpg.android.habitica.ui.adapter.tasks;

import com.habitrpg.android.habitica.R;
import com.habitrpg.android.habitica.helpers.RemindersManager;
import com.habitrpg.android.habitica.ui.helpers.ItemTouchHelperAdapter;
import com.habitrpg.android.habitica.ui.helpers.ItemTouchHelperViewHolder;
import com.magicmicky.habitrpgwrapper.lib.models.tasks.RemindersItem;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by keithholliday on 5/31/16.
 */
public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<RemindersItem> reminders = new ArrayList<>();
    private RemindersManager remindersManager;

    public RemindersAdapter(List<RemindersItem> remindersInc, String taskType) {
        reminders.addAll(remindersInc);
        remindersManager = new RemindersManager(taskType);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Date time = reminders.get(position).getTime();
        holder.reminderItemTextView.setText(remindersManager.reminderTimeToString(time));
        holder.hour = time.getHours();
        holder.minute = time.getMinutes();
    }

    public void addItem(RemindersItem item) {
        reminders.add(item);
        notifyItemInserted(reminders.size() - 1);
    }

    public List<RemindersItem> getRemindersItems() {
        return reminders;
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }


    @Override
    public void onItemDismiss(int position) {
        if (position >= 0 && position < reminders.size()) {
            reminders.get(position).async().delete();
            reminders.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(reminders, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, Button.OnClickListener {

        @BindView(R.id.item_edittext)
        EditText reminderItemTextView;

        @BindView(R.id.delete_item_button)
        Button deleteButton;

        int hour, minute;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            deleteButton.setOnClickListener(this);

            reminderItemTextView.setOnClickListener(v -> {
                RemindersItem reminder = reminders.get(getAdapterPosition());

                String taskType;

                if (reminder.getTask() == null) {
                    taskType = reminder.getType();
                } else {
                    taskType = reminder.getTask().getType();
                }

                remindersManager.createReminderTimeDialog(null, taskType, v.getContext(), reminder);
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            if (v == deleteButton) {
                RemindersAdapter.this.onItemDismiss(getAdapterPosition());
            }
        }
    }
}
