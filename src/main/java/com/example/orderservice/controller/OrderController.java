package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@Slf4j
public class OrderController {

    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;

    OrderProducer orderProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService,KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer=kafkaProducer;
        this.orderProducer=orderProducer;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("order Service port on %s",env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder orderReq){

        log.info("before add order data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        OrderDto orderDto=mapper.map(orderReq, OrderDto.class);
        orderDto.setUserId(userId);
        //UserDto userDto = mapper.map(user,UserDto.class);
        //jpa
        OrderDto resOrderDto= orderService.createOrder(mapper.map(orderDto, OrderDto.class));
        ResponseOrder responseOrder=mapper.map(resOrderDto,ResponseOrder.class);


        // kafka

//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderReq.getQty()*orderReq.getUnitPrice());


        // send this order to kafka
        kafkaProducer.send("example-catalog-topic",orderDto);
//        orderProducer.send("orders",orderDto);
//        ResponseOrder responseOrder=mapper.map(orderDto,ResponseOrder.class);

        log.info("after added order data");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) throws Exception {
        log.info("before retrieve order data");

        Iterable<OrderEntity> orderEntities=orderService.getOrdersByUserId(userId);
        List<ResponseOrder> responseOrders=new ArrayList<>();
        orderEntities.forEach(v->{
            responseOrders.add(new ModelMapper().map(v,ResponseOrder.class));
        });

//        try{
//            Thread.sleep(1000);
//            throw new Exception("error");
//        }
//        catch (InterruptedException ex){
//            log.warn(ex.getMessage());
//        }
        log.info("after retrieve order data");

        return ResponseEntity.status(HttpStatus.OK).body(responseOrders);
    }
}
