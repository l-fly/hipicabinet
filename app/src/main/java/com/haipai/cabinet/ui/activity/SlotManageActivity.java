package com.haipai.cabinet.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;

import butterknife.BindView;
import butterknife.OnClick;

public class SlotManageActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }

    @BindView(R.id.recycler_container)
    RecyclerView mRecyclerContainer;

    DisableAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_manage);
    }

    @Override
    public void initUIView() {
        mRecyclerContainer = findViewById(R.id.recycler_container);
        mAdapter = new DisableAdapter(this);
        mRecyclerContainer.setLayoutManager(new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerContainer.setAdapter(mAdapter);
        //todo
        mAdapter.notifyItemRangeInserted(0, 12);
    }

    @Override
    public void setCurrent() {

    }
    class DisableAdapter extends RecyclerView.Adapter<DisableAdapter.MyViewHold>{
        private Context mContext;
        public DisableAdapter(Context context){
            mContext = context;
        }

        @Override
        public MyViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyViewHold holder = new MyViewHold(LayoutInflater.from(
                    mContext).inflate(R.layout.item_open, parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHold holder, @SuppressLint("RecyclerView") int position) {
            holder.text.setText(""+(position+1));
            if(LocalDataManager.getInstance().isPortDisable(position)){
                holder.text.setTextColor(0xffff0000);
            }else{
                holder.text.setTextColor(0xff00ff00);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalDataManager.getInstance().setPortDisable(position, !LocalDataManager.getInstance().isPortDisable(position));
                    notifyDataSetChanged();
                    setLastActive();
                }
            });
        }

        @Override
        public int getItemCount() {
            //todo
            return 12;
        }

        class MyViewHold extends RecyclerView.ViewHolder{
            TextView text;
            public MyViewHold(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}