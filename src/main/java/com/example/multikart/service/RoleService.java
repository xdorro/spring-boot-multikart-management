package com.example.multikart.service;

import com.example.multikart.domain.dto.RoleRequestDTO;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface RoleService {
    String findAllRoles(Model model);

    String createRole(Model model);

    String storeRole(RoleRequestDTO input, BindingResult result, Model model, RedirectAttributes redirect);

    String editRole(Long id, Model model, RedirectAttributes redirect);

    String updateRole(Long id, RoleRequestDTO input, BindingResult result, Model model, RedirectAttributes redirect);

    String deleteRole(Long id, Model model, RedirectAttributes redirect);
}
