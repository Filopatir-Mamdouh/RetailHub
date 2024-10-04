package com.iti4.retailhub.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.iti4.retailhub.BuildConfig
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.Repository
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.datastorage.network.RemoteDataSourceImpl
import com.iti4.retailhub.userauthuntication.UserAuthuntication
import com.iti4.retailhub.userauthuntication.UserAuthunticationInterface
import com.iti4.retailhub.userlocalprofiledata.UserLocalProfileData
import com.iti4.retailhub.userlocalprofiledata.UserLocalProfileDataInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideRepository(remoteDataSource: RemoteDataSource , userAuthuntication: UserAuthunticationInterface,UserLocalProfileData: UserLocalProfileDataInterface): IRepository {
        return Repository(remoteDataSource, userAuthuntication,UserLocalProfileData)
    }


    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder().serverUrl("https://android-alex-team4.myshopify.com/admin/api/2024-10/graphql.json")
            .addHttpHeader("X-Shopify-Access-Token", BuildConfig.ADMIN_ACCESS_TOKEN)
            .build()
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apolloClient: ApolloClient): RemoteDataSource {
        return RemoteDataSourceImpl(apolloClient)
    }

    @Provides
    fun provideUserAuthuntication(@ApplicationContext context: Context): UserAuthunticationInterface {
        return UserAuthuntication(context)
    }
    @Provides
    fun provideUserLocalProfileData(@ApplicationContext context: Context): UserLocalProfileDataInterface {
        return UserLocalProfileData(context)
    }

}