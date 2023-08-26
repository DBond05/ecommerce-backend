package com.electratech.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.electratech.ecommerce.entity.Country;
import com.electratech.ecommerce.entity.Order;
import com.electratech.ecommerce.entity.Product;
import com.electratech.ecommerce.entity.ProductCategory;
import com.electratech.ecommerce.entity.State;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer{
	@Value("${allowed.origins}")
	private String [] theAllowedOrigins;
 private EntityManager entityManager;
 public MyDataRestConfig(EntityManager theEntityManager) {
	 entityManager = theEntityManager;
 }
	
	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
		HttpMethod[] theUnsupportedActions= {HttpMethod.PUT, HttpMethod.POST,
				HttpMethod.DELETE, HttpMethod.PATCH};
		
		disableHttpMethods(ProductCategory.class, config, theUnsupportedActions);
		disableHttpMethods(Product.class, config, theUnsupportedActions);
		disableHttpMethods(Country.class, config, theUnsupportedActions);
		disableHttpMethods(State.class, config, theUnsupportedActions);
		disableHttpMethods(Order.class, config, theUnsupportedActions);
		exposeIds(config);
		//configure cors mapping
		cors.addMapping(config.getBasePath()+ "/**").allowedOrigins(theAllowedOrigins);
	}

	private void disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
		config.getExposureConfiguration()
		.forDomainType(theClass)
		.withItemExposure((metdata, httpMethods)-> httpMethods.disable(theUnsupportedActions))
		.withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
	}
	
	//method to expose entity ids
	private void exposeIds(RepositoryRestConfiguration config) {
		//- get a list of all entity classes from the entity manager
		Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
		// - create an array of entity types
		List<Class> entityClasses = new ArrayList<>();
		// - for loop to get the entity types for the entities
		for(EntityType temp: entities) {
			entityClasses.add(temp.getJavaType());
		}
		//expose the entity ids for the array of entity/domain types
		Class[] domainTypes = entityClasses.toArray(new Class[0]);
		config.exposeIdsFor(domainTypes);
		
		
		
	}
}
