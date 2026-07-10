package com.Food.Model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
    @JsonIgnore
    private User customer;
	
	@ManyToOne
    @JsonIgnore
    private Restaurant restaurant;
	
    
    private Long totalAmount;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    
    private LocalDateTime createdAt;
    
    private Long deliveryPartnerId;
    
    private LocalDateTime acceptedAt;
    private LocalDateTime preparingAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime outForDeliveryAt;
    private LocalDateTime deliveredAt;
    
    @ManyToOne
    private Address deliveryAddress;
    
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderItem> items;


//    private Payment payment;
    private int TotalItem;
    private Long TotalPrice;
	
	
	    
	
	
	

}
