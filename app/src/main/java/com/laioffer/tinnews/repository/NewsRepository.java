package com.laioffer.tinnews.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.AppDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewsRepository {

    private final NewsApi newsApi;
    //for database
    private final AppDatabase database;
    private AsyncTask asyncTask;

    public NewsRepository(Context context) {

        newsApi = RetrofitClient.newInstance(context).create(NewsApi.class);
        //for database
        database = TinNewsApplication.getDatabase();
    }

    //Retrofit send request and let other thread to handle it, give to main thread when ready
    public LiveData<NewsResponse> getTopHeadlines(String country) {
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();

        newsApi.getTopHeadLines(country)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            topHeadlinesLiveData.setValue(response.body());
                        } else {
                            topHeadlinesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        topHeadlinesLiveData.setValue(null);
                    }
                });
        return topHeadlinesLiveData;
    }

    //Retrofit send request and let other thread to handle it, give to main thread when ready
    public LiveData<NewsResponse> searchNews(String query) {
        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40)
                .enqueue(
                        new Callback<NewsResponse>() {
                            @Override
                            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                                if (response.isSuccessful()) {
                                    everyThingLiveData.setValue(response.body());
                                } else {
                                    everyThingLiveData.setValue(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsResponse> call, Throwable t) {
                                everyThingLiveData.setValue(null);
                            }
                        });
        return everyThingLiveData;
    }

    // update database -- asyncTask let other thread to handle request and let UI thread take over when ready
    // thread pool controls how many thread we use now
    public LiveData<Boolean> favoriteArticle(Article article) {
        MutableLiveData<Boolean> isSuccessLiveData = new MutableLiveData<>();
        asyncTask =
                //<input, progress Integer, result long>
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... Voids) {
                        try {
                            database.dao().saveArticle(article);
                        } catch (Exception e) {
                            Log.e("text", e.getMessage());
                            return false;
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        article.favorite = isSuccess;
                        isSuccessLiveData.setValue(isSuccess);
                    }
                }.execute();
        return isSuccessLiveData;
    }

    // get data from db
    public LiveData<List<Article>> getAllSavedArticles() {
        return database.dao().getAllArticles();
    }

    // delete data from db
    public void deleteSavedArticle(Article article) {
        AsyncTask.execute(
                () -> database.dao().deleteArticle(article));
    }

    //cancel update database
    public void onCancel() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
    }
}
