package com.chuyeu.training.myapp.webapp.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chuyeu.training.myapp.datamodel.Order;
import com.chuyeu.training.myapp.datamodel.OrderItem;
import com.chuyeu.training.myapp.datamodel.ProductVariant;
import com.chuyeu.training.myapp.services.IOrderItemService;
import com.chuyeu.training.myapp.services.IOrderService;
import com.chuyeu.training.myapp.services.IProductVariantService;
import com.chuyeu.training.myapp.services.util.UserAuthStorage;
import com.chuyeu.training.myapp.webapp.models.OrderItemModel;
import com.chuyeu.training.myapp.webapp.models.parts.PriceQuantityModel;

@RestController
@RequestMapping("/order-item")
public class OrderItemController {

	private final Logger LOGGER = LoggerFactory.getLogger(OrderItemController.class);
	
	@Inject
	private IOrderItemService orderItemService;
	
	@Inject
	private IProductVariantService productVariantService;

	@Inject
	private IOrderService orderService;

	@Inject
	private ApplicationContext context;

	@Autowired
	ConversionService conversionService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllItems(@RequestParam(value = "order-id", required = false) Integer orderId) {

		List<OrderItem> listOrderItems = orderItemService.getAllByOrderId(orderId);
		List<OrderItemModel> listOrderItemsModel = new ArrayList<>();

		for (OrderItem orderItem : listOrderItems) {
			OrderItemModel orderItemModel = conversionService.convert(orderItem, OrderItemModel.class);
			listOrderItemsModel.add(orderItemModel);
		}
		LOGGER.info("Get all orderItems by order id ={}",orderId);
		return new ResponseEntity<List<OrderItemModel>>(listOrderItemsModel, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id) {
		OrderItem orderItem = orderItemService.get(id);
		OrderItemModel orderItemModel = conversionService.convert(orderItem, OrderItemModel.class);
		LOGGER.info("Get orderItem by  id ={}",id);
		return new ResponseEntity<OrderItemModel>(orderItemModel, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createOrderItem(@RequestBody OrderItemModel orderItemModel) {

		UserAuthStorage userAuthStorage = context.getBean(UserAuthStorage.class);
		Order order = orderService.get(orderItemModel.getOrderId());

		if (order.getUserProfileId().equals(userAuthStorage.getId())) {
			
			OrderItem orderItem = conversionService.convert(orderItemModel, OrderItem.class);
			orderItemService.saveOrUpdate(orderItem);
			LOGGER.info("OrderItem ={} was created", orderItem);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrderItem(@RequestBody PriceQuantityModel priceQuantityModel,
			@PathVariable(value = "id") Integer id) {

		OrderItem orderItem = orderItemService.get(id);
		UserAuthStorage userAuthStorage = context.getBean(UserAuthStorage.class);
		Order order = orderService.get(orderItem.getOrderId());

		if (order.getUserProfileId().equals(userAuthStorage.getId())) {

			Integer productVariantId = orderItem.getProductVariantId();
			ProductVariant productVariant = productVariantService.getProductVariant(productVariantId);
			if(productVariant.getAvailableQuantity()<priceQuantityModel.getQuantity()){
				return new ResponseEntity<Void>(HttpStatus.PRECONDITION_FAILED);
			} else {
				productVariant.setAvailableQuantity(productVariant.getAvailableQuantity()-priceQuantityModel.getQuantity());
				productVariantService.update(productVariant);
				LOGGER.info("Updete quantity for orderItem ={} with id ={}",orderItem, id);
			}
			orderItem.setOrderQuantity(priceQuantityModel.getQuantity());
			orderItem.setPrice(priceQuantityModel.getPrice());
			orderItemService.saveOrUpdate(orderItem);
			LOGGER.info("Update orderItem ={} with id ={}",orderItem, id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} else {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteOrderItem(@PathVariable(value = "id") Integer id) {

		OrderItem orderItem = orderItemService.get(id);
		UserAuthStorage userAuthStorage = context.getBean(UserAuthStorage.class);
		Order order = orderService.get(orderItem.getOrderId());
		
		if (order.getUserProfileId().equals(userAuthStorage.getId())) {
			orderItemService.delete(id);
			LOGGER.info("Delete orderItem ={} with id ={}",orderItem, id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} else {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
	}

}
