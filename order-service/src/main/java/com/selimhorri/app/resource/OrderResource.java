package com.selimhorri.app.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderResource {
	
	private final OrderService orderService;
	
	@GetMapping
	@CircuitBreaker(name = "orderService", fallbackMethod = "fallbackFindAll")
	public ResponseEntity<DtoCollectionResponse<OrderDto>> findAll() {
		log.info("*** OrderDto List, controller; fetch all orders *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderService.findAll()));
	}
	
	@GetMapping("/{orderId}")
	@Retry(name = "orderServiceRetry", fallbackMethod = "fallbackOrder")
	public ResponseEntity<OrderDto> findById(
			@PathVariable("orderId") 
			@NotBlank(message = "Input must not be blank") 
			@Valid final String orderId) {
		log.info("*** OrderDto, resource; fetch order by id *");
		return ResponseEntity.ok(this.orderService.findById(Integer.parseInt(orderId)));
	}
	
	@PostMapping
	@Bulkhead(name = "orderBulkhead", type = Bulkhead.Type.THREADPOOL)
	public ResponseEntity<OrderDto> save(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderDto orderDto) {
		log.info("*** OrderDto, resource; save order *");
		return ResponseEntity.ok(this.orderService.save(orderDto));
	}
	
	@PutMapping
	public ResponseEntity<OrderDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderDto orderDto) {
		log.info("*** OrderDto, resource; update order *");
		return ResponseEntity.ok(this.orderService.update(orderDto));
	}
	
	@PutMapping("/{orderId}")
	public ResponseEntity<OrderDto> update(
			@PathVariable("orderId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String orderId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderDto orderDto) {
		log.info("*** OrderDto, resource; update order with orderId *");
		return ResponseEntity.ok(this.orderService.update(Integer.parseInt(orderId), orderDto));
	}
	
	@DeleteMapping("/{orderId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId) {
		log.info("*** Boolean, resource; delete order by id *");
		this.orderService.deleteById(Integer.parseInt(orderId));
		return ResponseEntity.ok(true);
	}

	public ResponseEntity<OrderDto> fallbackOrder(String orderId, Exception ex) {
		log.warn("Fallback for findById due to: {}", ex.toString());
		return ResponseEntity.ok(new OrderDto());
	}

	public ResponseEntity<DtoCollectionResponse<OrderDto>> fallbackFindAll(Throwable ex) {
		log.warn("Fallback method invoked due to: {}", ex.toString());

		return ResponseEntity.ok(new DtoCollectionResponse<>(List.of()));

	}


}










