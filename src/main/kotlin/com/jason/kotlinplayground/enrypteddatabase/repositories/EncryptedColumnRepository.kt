package com.jason.kotlinplayground.enrypteddatabase.repositories

import com.jason.kotlinplayground.enrypteddatabase.models.EncryptedColumn
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

//Note: Had issues using CrudRepository with Id not autogenerating so it would cause a null constraint violation on save.
@Repository
interface EncryptedColumnRepository: JpaRepository<EncryptedColumn, Long> {}