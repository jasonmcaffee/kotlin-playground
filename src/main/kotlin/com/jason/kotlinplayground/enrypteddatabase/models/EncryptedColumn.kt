package com.jason.kotlinplayground.enrypteddatabase.models

import com.jason.kotlinplayground.enrypteddatabase.repositories.EncryptedColumnConverter
import javax.persistence.*


@Entity
class EncryptedColumn(
    //encrypt this value before it is stored to the db.
    @Convert(converter = EncryptedColumnConverter::class)
    val accessKey: String? = null,
    val description: String? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)

