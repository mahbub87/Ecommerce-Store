package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.repository.ItemRepository;
import com.example.demo.dao.service.ItemDAO;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Item;


@Service
public class CatalogService {
	
    @Autowired
    private ItemRepository itemRepository;
	@Autowired
	private ItemDAO itemDAO;

	public List<Item> getCatalogItems(Map<String, String> filters) {
        return itemDAO.getCatalogItems(filters);
	}

	public List<Item> getItemsByIds(List<String> itemIds) {
		return itemRepository.findAllById(itemIds);
	}

	public Item getItemById(String itemID) throws ProductNotFoundException {		
		Item item = itemRepository.findById(itemID).orElse(null);
        if (item != null) { return item; }

        // No item found
        throw new ProductNotFoundException();
	}

	public Item updateItemQuantity(String itemID, int newQuantity) throws ProductNotFoundException{
		Item existingItem = getItemById(itemID);
		existingItem.setQuantity(newQuantity);
		return itemRepository.save(existingItem);
	}
}