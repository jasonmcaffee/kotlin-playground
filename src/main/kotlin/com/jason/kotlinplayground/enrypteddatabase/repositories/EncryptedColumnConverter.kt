package com.jason.kotlinplayground.enrypteddatabase.repositories

import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter

//Todo: config entry
const val secretKey = "super-secret-key"

@Component
class EncryptedColumnConverter: AttributeConverter<String, String> {
    private var key = SecretKeySpec(secretKey.toByteArray(), "AES")
    private var cipher = Cipher.getInstance("AES")

    override fun convertToDatabaseColumn(attribute: String?): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedByteArray = cipher.doFinal(attribute?.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedByteArray)
    }

    override fun convertToEntityAttribute(dbData: String?): String {
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedValue = Base64.getDecoder().decode(dbData)
        val decryptedByteArray = cipher.doFinal(decodedValue)
        return String(decryptedByteArray)
    }
}