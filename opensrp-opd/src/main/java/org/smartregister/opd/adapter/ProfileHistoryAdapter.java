package org.smartregister.opd.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.opd.R;
import org.smartregister.opd.domain.ProfileHistory;
import org.smartregister.opd.holders.GroupedListableViewHolder;
import org.smartregister.opd.utils.VisitUtils;
import org.smartregister.view.ListContract;

import java.util.List;

import static org.smartregister.opd.utils.OpdUtils.context;

public class ProfileHistoryAdapter extends GroupedListableAdapter<ProfileHistory, GroupedListableViewHolder<ProfileHistory>> {

    public ProfileHistoryAdapter(List<ProfileHistory> items, ListContract.View<ProfileHistory> view) {
        super(items, view);
    }

    @Override
    public void reloadData(@Nullable List<ProfileHistory> items) {
        super.reloadData(items);
    }

    @NonNull
    @Override
    public GroupedListableViewHolder<ProfileHistory> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_history_row, parent, false);
        return new ProfileHistoryViewHolder(view);
    }

    public static class ProfileHistoryViewHolder extends GroupedListableViewHolder<ProfileHistory> {

        private View currentView;
        private TextView tvHeader;
        private TextView tvEvent;
        private TextView tvEdit;
        private ImageView tvIcon;

        private ProfileHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            currentView = itemView;
            tvHeader = itemView.findViewById(R.id.tvHeader);
            tvEvent = itemView.findViewById(R.id.tv_action);
            tvEdit = itemView.findViewById(R.id.tv_edit);
            tvIcon = itemView.findViewById(R.id.iv_history_Icon);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void bindView(ProfileHistory history, ListContract.View<ProfileHistory> view) {
            String visitType = VisitUtils.getTranslatedVisitTypeName(history.getEventType());
            if (visitType != null) {
                itemView.setVisibility(View.VISIBLE);
                tvEvent.setText(history.getEventTime() + " " + visitType);
                tvEdit.setOnClickListener(v -> view.onListItemClicked(history, v.getId()));
                tvIcon.setOnClickListener(null);
                tvEvent.setOnClickListener(null);

                //This might Change Depending on View Functionality
                if(history.getEventDate() != null && !history.getEventDate().equals(context().getStringResource(R.string.today))){
                    tvEdit.setText(context().getStringResource(R.string.view));
                }
                else {
                    tvEdit.setText(context().getStringResource(R.string.edit));
                }
            }  else {
                itemView.setVisibility(View.GONE);
            }

        }

        @Override
        public void resetView() {
            tvHeader.setText("");
            tvHeader.setVisibility(View.GONE);

            tvEvent.setText("");
            tvEdit.setOnClickListener(null);
        }

        @Override
        public void bindHeader(ProfileHistory currentObject, @Nullable ProfileHistory previousObject, ListContract.View<ProfileHistory> view) {
            if (previousObject == null || (currentObject.getEventDate() != null && !currentObject.getEventDate().equals(previousObject.getEventDate()))) {
                tvHeader.setVisibility(View.VISIBLE);
                tvHeader.setText(currentObject.getEventDate());
            }
        }
    }
}
