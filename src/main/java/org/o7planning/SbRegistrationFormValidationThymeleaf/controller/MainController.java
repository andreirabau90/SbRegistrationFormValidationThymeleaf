package org.o7planning.SbRegistrationFormValidationThymeleaf.controller;

import org.o7planning.SbRegistrationFormValidationThymeleaf.dao.AppUserDAO;
import org.o7planning.SbRegistrationFormValidationThymeleaf.dao.CountryDAO;
import org.o7planning.SbRegistrationFormValidationThymeleaf.formbean.AppUserForm;
import org.o7planning.SbRegistrationFormValidationThymeleaf.model.AppUser;
import org.o7planning.SbRegistrationFormValidationThymeleaf.model.Country;
import org.o7planning.SbRegistrationFormValidationThymeleaf.validator.AppUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private AppUserDAO appUserDAO;
    @Autowired
    private CountryDAO countryDAO;
    @Autowired
    private AppUserValidator appUserValidator;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target = " + target);
        if (target.getClass() == AppUserForm.class) {
            dataBinder.setValidator(appUserValidator);
        }
    }

    @RequestMapping("/")
    public String viewHome(Model model) {

        return "welcomePage";
    }
    @RequestMapping("/members")
public  String viewMembers(Model model){
        List<AppUser> list= appUserDAO.getAppUsers();
        model.addAttribute("members",list);
        return "membersPage";
    }

    @RequestMapping("/registerSuccessful")
    public String viewRegisterSuccessful(Model model){
        return "registerSuccessfulPage";
    }

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public  String viewRegister(Model model){

        AppUserForm form=new AppUserForm();
        List<Country> countries= countryDAO.getCountries();

        model.addAttribute("appUserForm",form);
        model.addAttribute("counties",countries);
        return "registerPage";
    }
@RequestMapping(value = "/register",method = RequestMethod.POST)
    public  String saveRegister(Model model,
                                @ModelAttribute("appUserForm") @Validated AppUserForm appUserForm,
                                BindingResult result,
                                final RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            List<Country> countries=countryDAO.getCountries();
            model.addAttribute("countries",countries);
            return "registerPage";
        }
        AppUser newUser=null;
        try{
            newUser= appUserDAO.createAppUser(appUserForm);
        }catch (Exception e){
            List<Country> countries=countryDAO.getCountries();
            model.addAttribute("countries",countries);
            model.addAttribute("errorMessage","Error: "+ e.getMessage());
            return "registerPage";
        }
        redirectAttributes.addFlashAttribute("flashUser",newUser);
        return "redirect:registerSuccessful";
    }


}
