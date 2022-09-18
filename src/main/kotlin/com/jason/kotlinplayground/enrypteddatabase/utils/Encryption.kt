package com.jason.kotlinplayground.enrypteddatabase.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class Encryption(val masterKeyString: String = "mastersecret-key", val encryptedDataKey: String = "ph+MAMnuu8mf/jX32N2ZI5os1VjqievzdiY45WGfhPk=") {
    private lateinit var dataKey: SecretKeySpec
    private lateinit var dataCipher: Cipher
    init {
        //decrypt the encrypted data key and instantiate dataKey and dataCipher
        val masterKey = SecretKeySpec(masterKeyString.toByteArray(), "AES")
        val masterCipher = Cipher.getInstance("AES")
        masterCipher.init(Cipher.DECRYPT_MODE, masterKey)
        val decodedEncryptedDataKey = Base64.getDecoder().decode(encryptedDataKey)
        val decryptedByteArray = masterCipher.doFinal(decodedEncryptedDataKey)
        val dataKeyString = String(decryptedByteArray)

        dataKey = SecretKeySpec(dataKeyString.toByteArray(), "AES")
        dataCipher = Cipher.getInstance("AES")
    }


    fun encryptString(str: String?): String{
        dataCipher.init(Cipher.ENCRYPT_MODE, dataKey)
        val encryptedByteArray = dataCipher.doFinal(str?.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedByteArray)
    }

    fun decryptString(encryptedStr: String?): String {
        dataCipher.init(Cipher.DECRYPT_MODE, dataKey)
        val decodedValue = Base64.getDecoder().decode(encryptedStr)
        val decryptedByteArray = dataCipher.doFinal(decodedValue)
        return String(decryptedByteArray)
    }


}

//help create the keys.  using gpg doesn't yield the same result so not sure if command line is an option...
fun encryptDataKey(masterKeyString: String, dataKeyString: String): String{
    val masterKey = SecretKeySpec(masterKeyString.toByteArray(), "AES")
    val masterCipher = Cipher.getInstance("AES")
    masterCipher.init(Cipher.ENCRYPT_MODE, masterKey)
    val encryptedByteArray = masterCipher.doFinal(dataKeyString.toByteArray())
    return Base64.getEncoder().encodeToString(encryptedByteArray)
}