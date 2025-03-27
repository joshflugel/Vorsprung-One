package com.josh25.vorsprungone.di

import com.josh25.vorsprungone.data.datasource.MissionPlannerDataSource
import com.josh25.vorsprungone.data.datasource.RoverMissionDataSource
import com.josh25.vorsprungone.data.network.MockApi
import com.josh25.vorsprungone.data.network.MockNetworkInterceptor
import com.josh25.vorsprungone.data.network.createMockApi
import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
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
    fun provideMockNetworkInterceptor(
        missionPlannerDataSource: MissionPlannerDataSource
    ): MockNetworkInterceptor {
        return MockNetworkInterceptor(missionPlannerDataSource)
    }

    @Provides
    @Singleton
    fun provideRoverRepository(dataSource: RoverMissionDataSource): MissionPlanRepository {
        return MissionPlanRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideExecuteRoverCommandsUseCase(repository: MissionPlanRepository): GetMissionSequenceUseCase {
        return GetMissionSequenceUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRoverViewModel(
        getMissionSequenceUseCase: GetMissionSequenceUseCase,
        getMissionPlanUseCase: GetMissionPlanUseCase
    ): MissionControlViewModel {
        return MissionControlViewModel(getMissionSequenceUseCase, getMissionPlanUseCase) // ✅ correct instance
    }
}
