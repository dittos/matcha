package org.sapzil.matcha.userprofile.repository

import org.sapzil.matcha.userprofile.model.UserProfile
import org.springframework.data.repository.CrudRepository

interface UserProfileRepository : CrudRepository<UserProfile, String>