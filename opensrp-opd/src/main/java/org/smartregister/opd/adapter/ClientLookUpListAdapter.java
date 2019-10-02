package org.smartregister.opd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.opd.R;
import org.smartregister.util.Utils;

import java.util.List;

public class ClientLookUpListAdapter extends RecyclerView.Adapter<ClientLookUpListAdapter.MyViewHolder> {

    private List<CommonPersonObject> data;
    private Context context;
    private static ClickListener clickListener;

    public ClientLookUpListAdapter(List<CommonPersonObject> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.opd_lookup_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        CommonPersonObject commonPersonObject = data.get(i);
        String firstName = Utils.getValue(commonPersonObject.getColumnmaps(), "first_name", true);
        String lastName = Utils.getValue(commonPersonObject.getColumnmaps(), "last_name", true);
        String nationalId = Utils.getValue(commonPersonObject.getColumnmaps(), "national_id", true);
        String opensrpId = Utils.getValue(commonPersonObject.getColumnmaps(), "opensrp_id", true);
        String fullname =  firstName + " " + lastName;
        String details = context.getString(R.string.national_id) + " - " + nationalId + ", " + context.getString(R.string.opensrp_Id_type) + " - " + opensrpId;
        holder.txtName.setText(fullname);
        holder.itemView.setTag(Utils.convert(commonPersonObject));
        holder.txtDetails.setText(details);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtDetails;

        private MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtDetails = view.findViewById(R.id.txtDetails);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onItemClick(view);
            }
        }
    }

    public interface  ClickListener {
        void onItemClick(View view);
    }

    public void setOnClickListener(ClickListener onClickListener){
        ClientLookUpListAdapter.clickListener = onClickListener;
    }
}