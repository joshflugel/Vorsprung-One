package com.josh25.vorsprungone.di

import com.josh25.vorsprungone.domain.repository.MissionRepository
import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    @Binds
    @Singleton
    abstract fun bindMissionRepository(
        impl: MissionPlanRepository
    ): MissionRepository
}