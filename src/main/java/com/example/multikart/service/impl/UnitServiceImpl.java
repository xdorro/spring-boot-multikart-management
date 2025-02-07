package com.example.multikart.service.impl;

import com.example.multikart.common.Const.DefaultStatus;
import com.example.multikart.common.DataUtils;
import com.example.multikart.domain.dto.UnitRequestDTO;
import com.example.multikart.domain.model.Unit;
import com.example.multikart.repo.UnitRepository;
import com.example.multikart.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class UnitServiceImpl implements UnitService {
    @Autowired
    private UnitRepository unitRepository;

    @Override
    public String findAllUnits(Model model) {
        var units = unitRepository.findAllByStatusNot(DefaultStatus.DELETED);
        model.addAttribute("units", units);

        return "backend/unit/index";
    }

    @Override
    public String createUnit(Model model) {
        model.addAttribute("unit", Unit.builder().status(DefaultStatus.ACTIVE).build());

        return "backend/unit/create";
    }

    @Override
    public String storeUnit(UnitRequestDTO input, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("unit", input);

            return "backend/unit/create";
        }

        var count = unitRepository.countByNameAndStatusNot(input.getName().trim(), DefaultStatus.DELETED);
        if (count > 0) {
            result.rejectValue("name", "", "Tên đơn vị đã được sử dụng");
        }

        if (result.hasErrors()) {
            model.addAttribute("unit", input);

            return "backend/unit/create";
        }

        var newUnit = new Unit(input);
        unitRepository.save(newUnit);

        redirect.addFlashAttribute("success", "Thêm thành công");
        return "redirect:/dashboard/units";
    }

    @Override
    public String editUnit(Long id, Model model, RedirectAttributes redirect) {
        var unit = unitRepository.findByUnitIdAndStatusNot(id, DefaultStatus.DELETED);
        if (DataUtils.isNullOrEmpty(unit)) {
            redirect.addFlashAttribute("error", "Đơn vị không tồn tại");

            return "redirect:/dashboard/units";
        }

        model.addAttribute("unit", unit);

        return "backend/unit/edit";
    }

    @Override
    public String updateUnit(Long id, UnitRequestDTO input, BindingResult result, Model model, RedirectAttributes redirect) {
        var unit = unitRepository.findByUnitIdAndStatusNot(id, DefaultStatus.DELETED);
        if (DataUtils.isNullOrEmpty(unit)) {
            redirect.addFlashAttribute("error", "Đơn vị không tồn tại");

            return "redirect:/dashboard/units";
        }

        if (result.hasErrors()) {
            input.setUnitId(id);
            model.addAttribute("unit", input);

            return "backend/unit/edit";
        }

        if (!unit.getName().equals(input.getName())) {
            var count = unitRepository.countByNameAndStatusNot(input.getName().trim(), DefaultStatus.DELETED);
            if (count > 0) {
                result.rejectValue("name", "", "Tên đơn vị đã được sử dụng");
            }

            unit.setName(input.getName());
        }

        if (result.hasErrors()) {
            input.setUnitId(id);
            model.addAttribute("unit", input);

            return "backend/unit/edit";
        }

        unit.setStatus(input.getStatus());
        unitRepository.save(unit);

        redirect.addFlashAttribute("success", "Sửa thành công");
        return "redirect:/dashboard/units";
    }

    @Override
    public String deleteUnit(Long id, Model model, RedirectAttributes redirect) {
        var unit = unitRepository.findByUnitIdAndStatusNot(id, DefaultStatus.DELETED);
        if (DataUtils.isNullOrEmpty(unit)) {
            redirect.addFlashAttribute("error", "Đơn vị không tồn tại");

            return "redirect:/dashboard/units";
        }

        unit.setStatus(DefaultStatus.DELETED);
        unitRepository.save(unit);

        redirect.addFlashAttribute("success", "Xóa thành công");
        return "redirect:/dashboard/units";
    }
}
