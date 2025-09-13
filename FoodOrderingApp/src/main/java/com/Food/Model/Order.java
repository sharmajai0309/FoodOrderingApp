package com.Food.Model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
    private User customer;
	
	@ManyToOne
    private Restaurant restaurant;
	
    
    private Long totalAmount;
    private String orderStatus;
    private Date createdAt;
    
    @ManyToOne
    private Address deliveryAddress;
    
    @OneToMany
    private List<OrderItem> items;
    
//    private Payment payment;
    private int TotalItem;
    private int TotalPrice;
	
	
	    
	
	
	

}
