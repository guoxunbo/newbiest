package com.newbiest.rms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.Recipe;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends IRepository<Recipe, String> {

}
