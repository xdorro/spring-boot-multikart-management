package com.example.vlmart.service.impl;

import com.example.vlmart.repo.UserRepository;
import com.example.vlmart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public String findAllUsers(Model model) {
        var users = userRepository.findAll();

        model.addAttribute("users", users);

        return "backend/user/index";
    }
}
