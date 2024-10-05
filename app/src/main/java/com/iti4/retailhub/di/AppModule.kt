package com.iti4.retailhub.di

import com.apollographql.apollo.ApolloClient
import com.iti4.retailhub.BuildConfig
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.Repository
import com.iti4.retailhub.datastorage.network.ApiService
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.datastorage.network.RemoteDataSourceImpl
import com.iti4.retailhub.datastorage.network.RetrofitDataSource
import com.iti4.retailhub.datastorage.network.RetrofitDataSourceImp
import com.iti4.retailhub.datastorage.network.RetrofitHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideRepository(
        remoteDataSource: RemoteDataSource,
        retrofitDataSource: RetrofitDataSource
    ): IRepository {
        return Repository(remoteDataSource, retrofitDataSource)
    }

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://android-alex-team4.myshopify.com/admin/api/2024-10/graphql.json")
            .addHttpHeader("X-Shopify-Access-Token", BuildConfig.ADMIN_ACCESS_TOKEN)
            .build()
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apolloClient: ApolloClient): RemoteDataSource {
        return RemoteDataSourceImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitHelper.retrofitInstance.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitDataSource(apiService: ApiService): RetrofitDataSource {
        return RetrofitDataSourceImp(apiService)
    }
}