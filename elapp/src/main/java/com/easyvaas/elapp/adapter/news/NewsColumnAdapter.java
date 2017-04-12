package com.easyvaas.elapp.adapter.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.CircleImageView;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 咨询---专栏Adapter
 */

public class NewsColumnAdapter extends MyBaseAdapter<NewsColumnModel.ColumnModel> {

    public NewsColumnAdapter(NewsColumnModel data) {
        super(data.getNews());
    }

    public void setData(NewsColumnModel data) {
        setNewData(data.getNews());
        notifyDataSetChanged();
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news_column, parent, false));
    }

    @Override
    protected void initOnItemClickListener() {

    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, NewsColumnModel.ColumnModel item) {
        if (helper instanceof ItemViewHolder) {
            ((ItemViewHolder)helper).setModel(item);
        }
    }

    public class ItemViewHolder extends BaseViewHolder {

        @BindView(R.id.news_column_cover)
        ImageView cover;
        @BindView(R.id.news_column_avatar)
        CircleImageView avatar;
        @BindView(R.id.news_column_nickname)
        TextView nickname;
        @BindView(R.id.news_column_introduce_user)
        TextView introduceUser;
        @BindView(R.id.news_column_title)
        TextView title;
        @BindView(R.id.news_column_introduce)
        TextView introduce;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setModel(NewsColumnModel.ColumnModel model) {
            if (model != null) {
                NewsColumnModel.Author author = model.getAuthor();
                if (author != null) {
                    Utils.showNewsImage(author.getAvatar(), avatar);
                    nickname.setText(author.getNickname());
                    introduceUser.setText(author.getIntroduce());
                    // // TODO: 2017/4/12 on click
                }
                Utils.showNewsImage(model.getCover(), cover);
                title.setText(model.getTitle());
                introduce.setText(model.getIntroduce());
                // // TODO: 2017/4/12 on click
            }
        }
    }
}
