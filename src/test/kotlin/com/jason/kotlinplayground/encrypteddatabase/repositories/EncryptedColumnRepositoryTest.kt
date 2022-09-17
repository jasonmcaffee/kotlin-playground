package com.jason.kotlinplayground.encrypteddatabase.repositories

import com.jason.kotlinplayground.enrypteddatabase.models.EncryptedColumn
import com.jason.kotlinplayground.enrypteddatabase.repositories.EncryptedColumnRepository
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase
class EncryptedColumnRepositoryTest(
    @Autowired val encryptedColumnRepository: EncryptedColumnRepository,
    @Autowired val jdbcTemplate: JdbcTemplate,
    ) {

    @BeforeAll
    fun setup(){}
    @AfterEach
    fun teardown(){}

    @Test fun `stores and reads encrypted column values`(){
        val entryToSave = EncryptedColumn("super secret key to encrypt", "public info")
        encryptedColumnRepository.save(entryToSave)
        val entries = encryptedColumnRepository.findAll()
        assert(entries.count() == 1)
        assert(entries.first().accessKey == "super secret key to encrypt")

        //raw query so we can validate data is encrypted.
        var encryptedValue: String? = null
        jdbcTemplate.query("select access_key from encrypted_column") {
            encryptedValue = it.getString("access_key")
        }
        assert(encryptedValue == "cnuD/b17Wc3mCtqhycOAKtyVQolDEnOcjjEcIbZg2/o=")
    }
}