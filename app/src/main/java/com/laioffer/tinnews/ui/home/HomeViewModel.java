package com.laioffer.tinnews.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;

public class HomeViewModel extends ViewModel {

    private final NewsRepository repository;
    private final MutableLiveData<String> countryInput = new MutableLiveData<>();

    // init favorite
    private final MutableLiveData<Article> favoriteArticleInput = new MutableLiveData<>();

    public HomeViewModel(NewsRepository newsRepository) {
        this.repository = newsRepository;
    }

    //listen
    public void setCountryInput(String country) {
        countryInput.setValue(country);
    }


    //save favorite
    public void setFavoriteArticleInput(Article article) {
        favoriteArticleInput.setValue(article);
    }


    public LiveData<NewsResponse> getTopHeadlines() {
        return Transformations.switchMap(countryInput, repository::getTopHeadlines);
    }

    // click favorite action
    public LiveData<Boolean > onFavorite() {
        return Transformations.switchMap(favoriteArticleInput, repository::favoriteArticle);
    }

    // cancel action
    public void onCancel() {
        repository.onCancel();
    }
}