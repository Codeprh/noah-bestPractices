package org.geekbang.time.commonmistakes.redundantcode.templatemethod.right;

import org.geekbang.time.commonmistakes.redundantcode.templatemethod.Cart;
import org.geekbang.time.commonmistakes.redundantcode.templatemethod.Db;
import org.geekbang.time.commonmistakes.redundantcode.templatemethod.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设计模式：模板方法
 * <p>
 * 我们在父类中实现了购物车处理的流程模板，然后把需 要特殊处理的地方留空白也就是留抽象方法定义，让子类去实现其中的逻辑。
 * 由于父类的逻 辑不完整无法单独工作，因此需要定义为抽象类。
 */
public abstract class AbstractCart {

    /**
     * 模板方法
     *
     * @param userId
     * @param items
     * @return
     */
    public Cart process(long userId, Map<Long, Integer> items) {

        Cart cart = new Cart();

        List<Item> itemList = new ArrayList<>();
        items.entrySet().stream().forEach(entry -> {
            Item item = new Item();
            item.setId(entry.getKey());
            item.setPrice(Db.getItemPrice(entry.getKey()));
            item.setQuantity(entry.getValue());
            itemList.add(item);
        });
        cart.setItems(itemList);

        itemList.stream().forEach(item -> {
            processCouponPrice(userId, item);
            processDeliveryPrice(userId, item);
        });

        cart.setTotalItemPrice(cart.getItems().stream().map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add));
        cart.setTotalDeliveryPrice(cart.getItems().stream().map(Item::getDeliveryPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        cart.setTotalDiscount(cart.getItems().stream().map(Item::getCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        cart.setPayPrice(cart.getTotalItemPrice().add(cart.getTotalDeliveryPrice()).subtract(cart.getTotalDiscount()));
        return cart;
    }

    /**
     * 抽象方法：计算优惠券
     *
     * @param userId
     * @param item
     */
    protected abstract void processCouponPrice(long userId, Item item);

    /**
     * 抽象方法：计算邮费
     *
     * @param userId
     * @param item
     */
    protected abstract void processDeliveryPrice(long userId, Item item);
}
