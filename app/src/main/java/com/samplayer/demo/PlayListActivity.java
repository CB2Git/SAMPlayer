package com.samplayer.demo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samplayer.SAMPlayer;
import com.samplayer.demo.databinding.ActivityPlayListBinding;

public class PlayListActivity extends AppCompatActivity {

    private ActivityPlayListBinding mBinding;

    private MyAdapter mMyAdapter = new MyAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_list);


        mBinding.playList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.playList.setAdapter(mMyAdapter);

        if (!SAMPlayer.getInstance().getServiceSession().isConnect()) {
            Toast.makeText(this, "服务没有连接", Toast.LENGTH_SHORT).show();
        } else {
            //SAMPlayer.getInstance().getPlayer().get
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_play_list, viewGroup, false);
            return new MyViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

    public static void start(Context context) {
        Bundle args = new Bundle();
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);
    }
}
