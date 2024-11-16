package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.ValidationException;
import com.example.servicehub.model.dto.EditProfileDto;
import com.example.servicehub.service.ServiceProviderService;
import com.example.servicehub.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller()
public class UserControllerTL {

    private final UserService userService;
    private final ServiceProviderService serviceProviderService;

    public UserControllerTL(UserService userService, ServiceProviderService serviceProviderService) {
        this.userService = userService;
        this.serviceProviderService = serviceProviderService;
    }

    @ModelAttribute
    public EditProfileDto editProfileDto(){return new EditProfileDto();}

    @GetMapping("/user/profile/{userId}")
    public String viewProfile(@PathVariable Long userId,
                              @AuthenticationPrincipal AppUser appUser,
                              Model model) {

        model.addAttribute("user", userService.getUserProfileById(userId).getBody());
        model.addAttribute("currentUserEmail", appUser.getUsername());

        boolean isServiceProvider = userService.isProviderWithId(userId);

        if (isServiceProvider) {
            model.addAttribute("serviceProviders", serviceProviderService.getServiceProvidersByUserId(userId));
        }
        else {
            model.addAttribute("noServiceMessage", "This user has not provided any services yet.");
        }

        return "profile";
    }

    @GetMapping("/user/profile/edit/{userId}")
    public String showEditProfileForm(
            @PathVariable Long userId,
            @AuthenticationPrincipal AppUser appUser, Model model) {

        if (!appUser.getUsername().equals(userService.findUserById(userId).getEmail())) {
            return "redirect:/service-provider";
        }

        model.addAttribute("user", userService.getUserDtoByUserId(userId));

        return "edit-profile";
    }

    @PostMapping("/user/profile/edit/{userId}")
    public String updateProfile(@PathVariable Long userId,
                                @ModelAttribute EditProfileDto editProfileDto,
                                @AuthenticationPrincipal AppUser appUser,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (!appUser.getUsername().equals(userService.findUserById(userId).getEmail())) {
            return "redirect:/service-provider";
        }

        try {
            userService.updateProfile(editProfileDto, appUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/user/profile/" + userId;
        }
        catch (ValidationException e) {
            editProfileDto.setId(appUser.id());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", editProfileDto);
            return "edit-profile";
        }
    }


}
