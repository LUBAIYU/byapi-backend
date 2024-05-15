package com.example.gateway.filter;

import com.example.common.constant.CommonConsts;
import com.example.common.constant.InterfaceConsts;
import com.example.common.model.entity.InterfaceInfo;
import com.example.common.model.entity.User;
import com.example.common.service.DubboInterfaceService;
import com.example.common.service.DubboUserInterfaceService;
import com.example.common.service.DubboUserService;
import com.example.common.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 自定义全局过滤器
 *
 * @author by
 */
@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private DubboUserService dubboUserService;
    @DubboReference
    private DubboInterfaceService dubboInterfaceService;
    @DubboReference
    private DubboUserInterfaceService dubboUserInterfaceService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求数据
        ServerHttpRequest request = exchange.getRequest();
        //获取接口地址
        String url = InterfaceConsts.INTERFACE_HOST + request.getPath().value();
        //获取接口方法
        String method = request.getMethodValue();
        //打印请求日志
        log.info("请求标识：" + request.getId());
        log.info("请求路径：" + url);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        //用户鉴权
        //获取请求头
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst(InterfaceConsts.ACCESS_KEY);
        User user = null;
        try {
            user = dubboUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.info("invokeUser error", e);
        }
        //判断用户是否存在
        if (user == null) {
            return handleNoAuth(exchange.getResponse());
        }
        //校验密钥
        String headerSign = headers.getFirst(InterfaceConsts.SIGN);
        String sign = SignUtil.generateSign(CommonConsts.BODY_KEY, user.getSecretKey());
        if (!sign.equals(headerSign)) {
            return handleNoAuth(exchange.getResponse());
        }
        //判断接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = dubboInterfaceService.getInterfaceInfo(url, method);
        } catch (Exception e) {
            log.info("invokeInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleInvokeError(exchange.getResponse());
        }
        return handleResponse(exchange, chain, interfaceInfo.getId(), user.getId());
    }

    /**
     * 获取响应结果，打印响应日志
     *
     * @param exchange        exchange
     * @param chain           chain
     * @param interfaceInfoId 接口ID
     * @param userId          用户ID
     * @return 获取响应结果
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //取出响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                //降级处理返回数据
                return chain.filter(exchange);
            }
            //定义一个响应结果装饰器
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                //等调用完转发的接口才会执行
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            try {
                                dubboUserInterfaceService.invokeCount(interfaceInfoId, userId);
                            } catch (Exception e) {
                                log.error("invokeCount error", e);
                            }
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            //释放掉内存
                            DataBufferUtils.release(dataBuffer);
                            // 构建返回日志
                            //响应数据
                            String responseData = new String(content, StandardCharsets.UTF_8);
                            log.info("响应结果：" + responseData);
                            return bufferFactory.wrap(content);
                        }));
                    } else {
                        log.error("<-- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            //转发请求，执行调用的接口
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
