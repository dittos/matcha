package org.sapzil.matcha.collection.repository

import org.sapzil.matcha.collection.model.Item
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository

interface ItemRepository : CrudRepository<Item, Long>, JpaSpecificationExecutor<Item>
