package com.jason.kotlinplayground.spring

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix="connection-health-job")
@ConfigurationPropertiesScan
data class JobConfigurationProperties(
     var maxConnectionsToRefreshPerRun: Int = 0,
     var daysUntilConnectionHealthIsConsideredStale: Int = 0,
     var onlyProcessConnectionsCreatedAfterThisDate: String = "",
     var maxConsecutiveErrorCount: Int = 0,
     var maxUpdatesPerSecond: Int = 0,
)


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase
class ConfigurationPropertiesTests(@Autowired val jobConfigurationProperties: JobConfigurationProperties) {
    @Test
    fun `loads properties from application properties`(){
        assert(jobConfigurationProperties.maxConnectionsToRefreshPerRun == 100)
        assert(jobConfigurationProperties.maxConsecutiveErrorCount == 25)
        assert(jobConfigurationProperties.daysUntilConnectionHealthIsConsideredStale == 14)
        assert(jobConfigurationProperties.onlyProcessConnectionsCreatedAfterThisDate == "2022-01-01")
        assert(jobConfigurationProperties.maxUpdatesPerSecond == 4)
    }
}