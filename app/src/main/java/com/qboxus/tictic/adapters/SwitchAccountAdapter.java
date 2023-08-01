package com.qboxus.tictic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.MultipleAccountModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

public class SwitchAccountAdapter extends RecyclerView.Adapter<SwitchAccountAdapter.ViewHolder> {

    private ArrayList<MultipleAccountModel> list;
    private AdapterClickListener click;

    public SwitchAccountAdapter(ArrayList<MultipleAccountModel> list, AdapterClickListener click) {
        this.list = list;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_switch_account_item_view, parent,false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MultipleAccountModel model = list.get(position);

        String picUrl = model.getuPic();
        if (!picUrl.contains(Variables.http)) {
            picUrl = Constants.BASE_URL + picUrl;
        }

        if (picUrl != null && !picUrl.equals("")) {

            holder.ivProfileImg.setController(Functions.frescoImageLoad(picUrl,holder.ivProfileImg,false));

        }
        holder.tvUserName.setText(model.getuName());
        holder.tvFullName.setText(model.getfName()+" "+model.getlName());
        if (model.isCheck()){
            holder.ivTick.setVisibility(View.VISIBLE);
        }else {
            holder.ivTick.setVisibility(View.GONE);
        }

        holder.bind(position, model, click);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName,tvFullName;
        ImageView ivTick;
        SimpleDraweeView ivProfileImg;
        View mainLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTick = itemView.findViewById(R.id.iv_tick);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            ivProfileImg=itemView.findViewById(R.id.user_image);
            mainLayout=itemView.findViewById(R.id.mainLayout);
        }


        public void bind(final int pos, final Object model,
                         final AdapterClickListener listener) {
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,model);
                }

            });

        }

    }
}