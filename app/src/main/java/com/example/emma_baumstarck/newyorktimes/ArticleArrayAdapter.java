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

/**
 * Created by emma_baumstarck on 8/10/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    private static class ViewHolder {
        ImageView imageView;
        TextView title;
    }


    public ArticleArrayAdapter(Context context, List<Article> articles){
        super(context, android.R.layout.simple_list_item_1,  articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tvTitle);
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
