package com.example.demo.dao.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.demo.dao.repository.ItemRepository;
import com.example.demo.model.Item;

@Service
public class ItemDAO {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
	private MongoTemplate mongoTemplate;

	public List<Item> getCatalogItems(Map<String, String> filters) {
		if (filters.size() == 0) { return itemRepository.findAll(); }

		Query query = new Query();

		Iterator<String> filterKeys = filters.keySet().iterator();
		while(filterKeys.hasNext()) {
			String currFilter = filterKeys.next();
			query.addCriteria(Criteria.where(currFilter).is(filters.get(currFilter)));
		}
		
		List<Item> items = mongoTemplate.find(query, Item.class);

        return items;
	}
}
