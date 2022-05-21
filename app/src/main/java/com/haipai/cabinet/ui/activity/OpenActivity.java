package com.haipai.cabinet.ui.activity;

import androidx.annotation.NonNull;
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
import com.haipai.cabinet.util.CustomMethodUtil;

public class OpenActivity extends BaseActivity {
    RecyclerView mRecyclerContainer;
    OpenAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
    }

    @Override
    public void initUIView() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerContainer = findViewById(R.id.recycler_container);
        mAdapter = new OpenAdapter(this);
        mRecyclerContainer.setLayoutManager(new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerContainer.setAdapter(mAdapter);
        mAdapter.notifyItemRangeInserted(0, LocalDataManager.slotNum);

    }

    @Override
    public void setCurrent() {

    }
    class OpenAdapter extends RecyclerView.Adapter<OpenAdapter.MyViewHold>{
        private Context mContext;
        public OpenAdapter(Context context){
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
            if(!CustomMethodUtil.isOpen(position)){
                holder.text.setTextColor(0xffffffff);
                holder.text.setBackgroundColor(0xff000000);
            }else{
                holder.text.setTextColor(0xffff0000);
                holder.text.setBackgroundColor(0xff999999);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CustomMethodUtil.open(position);
                    setLastActive();
                }
            });
        }

        @Override
        public int getItemCount() {
            return LocalDataManager.slotNum;
        }

        class MyViewHold extends RecyclerView.ViewHolder{
            TextView text;
            public MyViewHold(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
    @Override
    public void passSecond() {
        super.passSecond();
        mAdapter.notifyDataSetChanged();
    }
}