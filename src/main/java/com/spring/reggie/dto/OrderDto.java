package com.spring.reggie.dto;

import com.spring.reggie.entity.OrderDetail;
import lombok.Data;

import java.util.List;
@Data
public class OrderDto {

    private List<OrderDetail> orderDetails;
}
