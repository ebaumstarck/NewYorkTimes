package com.example.emma_baumstarck.newyorktimes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by emma_baumstarck on 8/10/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {
     static class ViewHolder {
        @BindView(R.id.ivImage) ImageView imageView;
        @BindView(R.id.tvTitle) TextView title;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ArticleArrayAdapter(Context context, List<Article> articles){
        super(context, android.R.layout.simple_list_item_1,  articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(article.getHeadline());
        viewHolder.imageView.setImageResource(0);
        String thumbnail = article.getThumbNail();

        if(!TextUtils.isEmpty(thumbnail)){
            Picasso.with(getContext()).load(thumbnail).fit().centerCrop()
                    .into(viewHolder.imageView);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
