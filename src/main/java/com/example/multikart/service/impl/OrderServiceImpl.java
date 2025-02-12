package com.example.multikart.service.impl;

import com.example.multikart.common.Const.DefaultStatus;
import com.example.multikart.common.Const.OrderStatus;
import com.example.multikart.common.DataUtils;
import com.example.multikart.common.Utils;
import com.example.multikart.domain.dto.ScreenRedis;
import com.example.multikart.repo.OrderDetailRepository;
import com.example.multikart.repo.OrderRepository;
import com.example.multikart.repo.ProductRepository;
import com.example.multikart.service.OrderService;
import com.example.multikart.service.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RedisCache redisCache;

    @Override
    public String findAllOrders(Model model) {
        var orders = orderRepository.findAllByStatusNot(OrderStatus.DELETED, DefaultStatus.ACTIVE);
        model.addAttribute("orders", orders);

        return "backend/order/index";
    }

    @Override
    public String viewOrder(Long id, Model model, RedirectAttributes redirect) {
        var order = orderRepository.findByOrderIdAndStatusNot(id, OrderStatus.DELETED, DefaultStatus.ACTIVE);
        if (DataUtils.isNullOrEmpty(order)) {
            redirect.addFlashAttribute("error", "Hóa đơn không tồn tại");

            return "redirect:/dashboard/orders";
        }

        model.addAttribute("order", order);

        var orderDetails = orderDetailRepository.findAllByOrderIdAndStatusNot(id, OrderStatus.DELETED);
        model.addAttribute("orderDetails", orderDetails);

        return "backend/order/detail";
    }

    @Override
    public String frontendViewOrder(Long id, HttpSession session, Model model, RedirectAttributes redirect) {
        var customer = Utils.getCustomerSession(session);

        var order = orderRepository.findByOrderIdAndCustomerIdAndStatusNot(id, customer.getCustomerId(), OrderStatus.DELETED, DefaultStatus.ACTIVE);
        if (DataUtils.isNullOrEmpty(order)) {
            redirect.addFlashAttribute("error", "Hóa đơn không tồn tại");

            return "redirect:/";
        }
        model.addAttribute("order", order);

        var orderDetails = orderDetailRepository.findAllByOrderIdAndStatusNot(id, OrderStatus.DELETED);
        model.addAttribute("orderDetails", orderDetails);

        return "frontend/order-success";
    }

    @Override
    public String frontendListOrder(HttpSession session, Model model, RedirectAttributes redirect) {
        var customer = Utils.getCustomerSession(session);

        var orders = orderRepository.findAllByCustomerIdAndStatusNot(customer.getCustomerId(), OrderStatus.DELETED, DefaultStatus.ACTIVE);
        model.addAttribute("orders", orders);

        return "frontend/order";
    }

    @Override
    @Transactional
    public String update(Long id, Integer status, BindingResult result, Model model, RedirectAttributes redirect) {
        var order = orderRepository.findByOrderIdAndStatusNot(id, OrderStatus.DELETED, DefaultStatus.ACTIVE);
        if (DataUtils.isNullOrEmpty(order)) {
            redirect.addFlashAttribute("error", "Hóa đơn không tồn tại");

            return "redirect:/dashboard/orders";
        }

        orderRepository.updateStatusByOrderIdAndStatusNot(id, status, OrderStatus.DELETED);

        if (Objects.equals(status, OrderStatus.CANCELED)) {
            var orderDetails = orderDetailRepository.findAllByOrderIdAndStatusNot(id, OrderStatus.DELETED);

            orderDetails.parallelStream().forEach(orderDetail -> {
                productRepository.updatePlusByProductIdAndAmountAndStatus(orderDetail.getProductId(), orderDetail.getQuantity(), DefaultStatus.ACTIVE);
            });
        }

        redisCache.delete(ScreenRedis.HOME.name());
        redisCache.delete(ScreenRedis.PRODUCT.name());

        redirect.addFlashAttribute("success", "Cập nhật thành công");
        return "redirect:/dashboard/orders/" + id;
    }
}
