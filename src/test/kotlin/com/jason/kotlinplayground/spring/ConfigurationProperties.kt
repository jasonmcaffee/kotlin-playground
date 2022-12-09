package com.jason.kotlinplayground.spring

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Pattern
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.xml.validation.Validator

// import javax.validation.Valid
// import javax.validation.constraints.Max
// import javax.validation.constraints.Min

// @Validated
// @Configuration
// @ConfigurationProperties(prefix="connection-health-job")
// @ConfigurationPropertiesScan
// data class JobConfigurationProperties(
//     @Min(1)
//     var maxConnectionsToRefreshPerRun: Int = -1,
//     var daysUntilConnectionHealthIsConsideredStale: Int = 0,
//     var onlyProcessConnectionsCreatedAfterThisDate: String = "",
//     var maxConsecutiveErrorCount: Int = 0,
//     var maxUpdatesPerSecond: Int = 0,
// )

// @Validated
// @ConstructorBinding
// @ConfigurationProperties(prefix="connection-health-job")
// data class JobConfigurationProperties(
//     @Min(10)
//     @Max(5)
//     var maxConnectionsToRefreshPerRun: Int,
//     var daysUntilConnectionHealthIsConsideredStale: Int,
//     var onlyProcessConnectionsCreatedAfterThisDate: String,
//     var maxConsecutiveErrorCount: Int,
//     var maxUpdatesPerSecond: Int,
// )
@Validated
@Component
@ConfigurationProperties(prefix="connection-health-job", ignoreUnknownFields = false)
class JobConfigurationProperties{
    @Min(10)
    var maxConnectionsToRefreshPerRun: Int = 0
    var daysUntilConnectionHealthIsConsideredStale: Int = 0
    // @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])\$")
    var onlyProcessConnectionsCreatedAfterThisDate: String = ""
    var maxConsecutiveErrorCount: Int = 0
    var maxUpdatesPerSecond: Int = 0
}

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase
@ConfigurationPropertiesScan
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