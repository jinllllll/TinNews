package com.laioffer.tinnews.ui.save;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.model.Article;

import java.util.ArrayList;
import java.util.List;

public class SavedNewsAdapter extends RecyclerView.Adapter<SavedNewsAdapter.SavedNewsViewHolder> {

    // cb step 1: call back listener interface
    interface OnClickListener {
        void onClick(Article article);

        void unLike(Article article);
    }

    private List<Article> articles = new ArrayList<>();
    // cb step 2: init onClickListener
    private OnClickListener onClickListener;

    public void setArticles(List<Article> articles) {
        this.articles.clear();
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }

    // cb step 3: new a listener every time
    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
        System.out.println("new listener created");
    }

    @NonNull
    @Override
    public SavedNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_news_item, parent, false);
        return new SavedNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedNewsViewHolder holder, int position) {

        Article article = articles.get(position);
        holder.author.setText(article.author);
        holder.description.setText(article.description);
        if (article.favorite) {
            holder.icon.setImageResource(R.drawable.ic_favorite_black_24dp);
            // cb step 4: set the hook here for callback -- inline way
            holder.icon.setOnClickListener(v -> {
                onClickListener.unLike(article);
                System.out.println("unlike button clicked");
            });
        } else {
            holder.icon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class SavedNewsViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView description;
        ImageView icon;

        public SavedNewsViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.image);
        }
    }
}
