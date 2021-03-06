package com.lexing.lexingframe.ganhuo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lexing.common.assist.L;
import com.lexing.lexingframe.R;
import com.lexing.lexingframe.SchedlersCompat;
import com.lexing.lexingframe.core.ServiceGenerator;
import com.lexing.lrecyclerview.recyclerview.ProgressStyle;
import com.lexing.lrecyclerview.recyclerview.XRecyclerView;

import java.util.List;

import rx.SingleSubscriber;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class GanhuoFragment extends Fragment {

    private XRecyclerView mRecyclerView;
    private AndroidAdapter mAdapter;
    private int index = 1;

    public GanhuoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        L.init(true, "ganhuo");
        View _view = inflater.inflate(R.layout.fragment_ganhuo, container, false);
        initViews(_view);
        getInfo();
        return _view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initViews(View rootView) {
        mRecyclerView = (XRecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.recyclerview_header, (ViewGroup) getActivity().findViewById(R.id.container), false);
        mRecyclerView.addHeaderView(header);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                index = 1;
                getInfo();
            }

            @Override
            public void onLoadMore() {
                getInfo();
            }
        });
        mAdapter = new AndroidAdapter(mRecyclerView, null, R.layout.item_ganhuo_android);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setPullRefreshEnabled(true);
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setRefreshing(true);
    }

    private void getInfo() {
        ServiceGenerator.ganhuoService()
                .fetchAndroidGanhuo(10, index)
                .map(new Func1<GanhuoData<List<GanhuoAndroid>>, List<GanhuoAndroid>>() {
                    @Override
                    public List<GanhuoAndroid> call(GanhuoData<List<GanhuoAndroid>> listGanhuoData) {
                        L.i(Thread.currentThread().getName());
                        return listGanhuoData.getData();
                    }
                })
                .compose(SchedlersCompat.<List<GanhuoAndroid>>applySingleExecutorSchedulers())
                .subscribe(new SingleSubscriber<List<GanhuoAndroid>>() {
                    @Override
                    public void onSuccess(List<GanhuoAndroid> value) {
                        if (index != 1) {
                            mAdapter.addAll(value);
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.refresh(value);
                            mRecyclerView.refreshComplete();
                            mAdapter.notifyDataSetChanged();
                        }

                        index++;
                    }

                    @Override
                    public void onError(Throwable error) {
                        L.e(error.getMessage());
                    }
                });

    }

}
