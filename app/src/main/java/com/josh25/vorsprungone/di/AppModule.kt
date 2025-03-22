package com.josh25.vorsprungone.di

import com.josh25.vorsprungone.data.datasource.RoverMissionDataSource
import com.josh25.vorsprungone.data.network.MockApi
import com.josh25.vorsprungone.data.network.MockNetworkInterceptor
import com.josh25.vorsprungone.data.network.createMockApi
import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.usecase.ExecuteRoverCommandsUseCase
import com.josh25.vorsprungone.presentation.TerrainGridViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMockNetworkInterceptor(): MockNetworkInterceptor {
        return MockNetworkInterceptor()
    }

    @Provides
    @Singleton
    fun provideMockApi(interceptor: MockNetworkInterceptor): MockApi {
        return createMockApi(interceptor)
    }

    @Provides
    @Singleton
    fun provideRoverMissionDataSource(api: MockApi): RoverMissionDataSource {
        return RoverMissionDataSource(api)
    }

    @Provides
    @Singleton
    fun provideRoverRepository(dataSource: RoverMissionDataSource): MissionPlanRepository {
        return MissionPlanRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideExecuteRoverCommandsUseCase(): ExecuteRoverCommandsUseCase {
        return ExecuteRoverCommandsUseCase()
    }

    @Provides
    @Singleton
    fun provideRoverViewModel(repository: MissionPlanRepository, useCase: ExecuteRoverCommandsUseCase): TerrainGridViewModel {
        return TerrainGridViewModel(repository, useCase)
    }
}
