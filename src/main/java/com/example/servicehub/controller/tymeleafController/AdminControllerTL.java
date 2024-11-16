package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.ChangeRoleDto;
import com.example.servicehub.model.enumeration.Role;
import com.example.servicehub.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminControllerTL {

    private final AdminService adminService;

    public AdminControllerTL(AdminService adminService) {
        this.adminService = adminService;
    }

    @ModelAttribute
    public ChangeRoleDto changeRoleDto() {
        return new ChangeRoleDto();
    }

    @GetMapping()
    public String adminPanel(Model model, @AuthenticationPrincipal AppUser appUser) {

        model.addAttribute("allUsers", adminService.findAllUsers());
        model.addAttribute("roleDropDown", List.of(Role.ADMIN, Role.USER));
        model.addAttribute("currentUserId", appUser.id());

        return "admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String adminChangeUserRole(@PathVariable Long id,
                                      @Valid ChangeRoleDto changeRoleDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("changeRoleDto", changeRoleDto);

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changeRoleDto",
                    bindingResult);

            return "redirect:/admin";
        }

        adminService.adminChangeUserRole(changeRoleDto, id);

        redirectAttributes.addFlashAttribute("message", "Role has been successfully changed.");

        return "redirect:/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal AppUser appUser,
                             RedirectAttributes redirectAttributes) {

        ResponseEntity<String> response = adminService.deleteUser(id, appUser);

        redirectAttributes.addFlashAttribute("message", response.getBody());

        return "redirect:/admin";
    }
}
