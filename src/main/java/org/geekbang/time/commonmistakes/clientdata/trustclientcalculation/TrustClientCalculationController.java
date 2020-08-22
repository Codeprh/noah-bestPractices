package org.geekbang.time.commonmistakes.clientdata.trustclientcalculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RequestMapping("trustclientcalculation")
@RestController
public class TrustClientCalculationController {

    /**
     * 错误实现：完全使用客户端的传递过来数据（不安全）
     *
     * @param order
     */
    @PostMapping("/order")
    public void wrong(@RequestBody Order order) {
        this.createOrder(order);
    }

    /**
     * 正确实现：服务器校验客户端传递过来的参数，服务器计算价格
     *
     * @param order
     */
    @PostMapping("/orderRight")
    public void right(@RequestBody Order order) {
        Item item = Db.getItem(order.getItemId());
        if (!order.getItemPrice().equals(item.getItemPrice())) {
            throw new RuntimeException("您选购的商品价格有变化，请重新下单");
        }
        order.setItemPrice(item.getItemPrice());
        BigDecimal totalPrice = item.getItemPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
        if (order.getItemTotalPrice().compareTo(totalPrice) != 0) {
            throw new RuntimeException("您选购的商品总价有变化，请重新下单");
        }
        order.setItemTotalPrice(totalPrice);
        createOrder(order);
    }

    /**
     * 正确实现：客户端只传递需要的参数，而不是传入一个大而全的参数
     *
     * @param createOrderRequest
     * @return
     */
    @PostMapping("orderRight2")
    public Order right2(@RequestBody CreateOrderRequest createOrderRequest) {
        Item item = Db.getItem(createOrderRequest.getItemId());
        Order order = new Order();
        order.setItemPrice(item.getItemPrice());
        order.setItemTotalPrice(item.getItemPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        createOrder(order);
        return order;
    }

    private void createOrder(Order order) {

    }
}
