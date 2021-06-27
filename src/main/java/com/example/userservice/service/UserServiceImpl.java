package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import com.netflix.discovery.converters.Auto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    Environment env;
    RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, Environment env
            , RestTemplate restTemplate, OrderServiceClient orderServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient=orderServiceClient;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        String userId=UUID.randomUUID().toString();
        userDto.setUserId(userId);

        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = mapper.map(userDto,UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(userEntity);

        UserDto resUserDto=mapper.map(userEntity,UserDto.class);
        return resUserDto;
    }

    @Override
    public UserDto getUserById(String userId) {
        UserEntity userEntity=userRepository.findByUserId(userId);

        if(userEntity==null)
            throw new UsernameNotFoundException("user not found");
        UserDto userDto=new ModelMapper().map(userEntity, UserDto.class);

//        List<ResponseOrder> orders=new ArrayList<>();

        // 1. MSA간 통신 첫번째 방법 : Rest template 사용
        /*
        String orderUrl=String.format(env.getProperty("order_service.url"),userId);
        ResponseEntity<List<ResponseOrder>> responseOrders= restTemplate.exchange(orderUrl, HttpMethod.GET, null
                , new ParameterizedTypeReference<List<ResponseOrder>>() {
        });

        List<ResponseOrder> orders=responseOrders.getBody();*/

        // 2. MSA 간 통신 두번째 방법 : Feign Client
         /*
          List<ResponseOrder> orders=null;
        // feign client로 호출 오류가 나도 orders를 제외한 부분은 정상 반환해주도록 구현
       try {
            orders = orderServiceClient.getOrders(userId);
        }catch (FeignException ex){
            log.error(ex.getMessage());
        }
        */
        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);
       userDto.setOrders(orders);
        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity=userRepository.findByEmail(username);
        if(userEntity==null){
            throw new UsernameNotFoundException(username);
        }
        return new User(userEntity.getEmail(),userEntity.getEncryptedPwd(),
                true, true, true, true, new ArrayList<>());

    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity=userRepository.findByEmail(email);
        if(userEntity==null){
            throw new UsernameNotFoundException(email);
        }
        UserDto userDto=new ModelMapper().map(userEntity,UserDto.class);
        return userDto;
    }
}
