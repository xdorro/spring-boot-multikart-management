package com.example.multikart.service;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

public interface OrderService {
    String findAllOrders(Model model);

    String viewOrder(Long id, Model model, RedirectAttributes redirect);

    String frontendViewOrder(Long id, HttpSession session, Model model, RedirectAttributes redirect);

    String frontendListOrder(HttpSession session, Model model, RedirectAttributes redirect);

    String update(Long id, Integer status, BindingResult result, Model model, RedirectAttributes redirect);
}
