package com.derteuffel.solutionafrica.controllers;

import com.derteuffel.solutionafrica.entities.Post;
import com.derteuffel.solutionafrica.entities.User;
import com.derteuffel.solutionafrica.helpers.UserDto;
import com.derteuffel.solutionafrica.repositories.PostRepository;
import com.derteuffel.solutionafrica.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("login")
    public String login(Model model){
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }
        model.addAttribute("footerPosts", footerPosts);
        return "login";
    }

    @GetMapping("users/lists")
    public String findAll(Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());

        System.out.println(user.getUsername());
        model.addAttribute("lists", userService.findAll());
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }
        model.addAttribute("connectedUser", user);
        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("userDto", new UserDto());

        return "users/lists";
    }

    @PostMapping("users/save")
    public String save(UserDto userDto, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if (user!= null){
            redirectAttributes.addFlashAttribute("error","Il existe deja un utilisateur enregistrer avec ce nom d'utilisateur et/ou cet adresse email ");
        }else {
            userDto.setPassword("0000");
            userService.save(userDto);
            redirectAttributes.addFlashAttribute("success", "Vous avez enregistrer avec succes un nouvel utilisateur");
        }
        return "redirect:/admin/users/lists";
    }

    public User update(User user) {
        return userService.update(user);
    }

    @GetMapping("users/activate/{id}")
    public String desactiveUser(@PathVariable Long id, RedirectAttributes redirectAttributes){
        User user = userService.getOne(id);
        if (user.getStatus() == true) {
            user.setStatus(false);
            redirectAttributes.addFlashAttribute("success", "Vous avez desactiver l'acces de l'utilisateur: "+ user.getUsername());
        }else {
            user.setStatus(true);
            redirectAttributes.addFlashAttribute("success", "Vous avez activer l'acces de l'utilisateur: "+ user.getUsername());
        }
        userService.update(user);
        return "redirect:/admin/users/lists";

    }

    @GetMapping("/users/details/{id}")
    public String findOne(@PathVariable Long id, Model model){
        User user = userService.getOne(id);
        model.addAttribute("user", user);
        return "users/details";
    }

    @GetMapping("users/role/update")
    public String roleUpdate(Long username, String role, RedirectAttributes redirectAttributes){
        User user = userService.getOne(username);
        user.getRoles().clear();
        user.getRoles().add(role);
        userService.update(user);
        redirectAttributes.addFlashAttribute("success", "Vous avez changer le role de "+user.getUsername()+" en :"+user.getRoles().get(0));
        return "redirect:/admin/users/lists";
    }

}
