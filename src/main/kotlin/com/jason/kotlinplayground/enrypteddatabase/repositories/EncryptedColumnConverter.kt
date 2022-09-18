package com.jason.kotlinplayground.enrypteddatabase.repositories

import com.jason.kotlinplayground.enrypteddatabase.utils.Encryption
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter

//Todo: config entry
//const val secretKey = "super-secret-key"

@Component
class EncryptedColumnConverter(
    @Value("\${encryption.master-key}") masterKey: String,
    @Value("\${encryption.encrypted-data-key}") encryptedDataKey: String
): AttributeConverter<String, String> {
    private var encryption = Encryption(masterKey, encryptedDataKey)

    override fun convertToDatabaseColumn(attribute: String?): String {
        return encryption.encryptString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): String {
        return encryption.decryptString(dbData)
    }
}