package com.derteuffel.solutionafrica.controllers;

import com.derteuffel.solutionafrica.entities.Post;
import com.derteuffel.solutionafrica.entities.User;
import com.derteuffel.solutionafrica.repositories.PostRepository;
import com.derteuffel.solutionafrica.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Value("${file.upload-dir}")
    private String location ;
    @Value("${backendServer}")
    private String backendBase ;


    @Autowired
    private UserService userService;


    @GetMapping("/lists")
    public String getAll(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes){

        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        System.out.println(user.getUsername());
        if (user.getStatus() == false){
            redirectAttributes.addFlashAttribute("error", "Votre compte est desactiver veillez contacte l'administrateur de ce site");
            return "redirect:/admin/login";
        }
        model.addAttribute("connectedUser", user);
        model.addAttribute("post", new Post());
        model.addAttribute("lists", postRepository.findAll());
        return "posts/lists";
    }

    @GetMapping("/lists/{category}")
    public String getAllByCategory(Model model, @PathVariable String category, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        System.out.println(category);
        model.addAttribute("connectedUser", user);
        model.addAttribute("post", new Post());
        model.addAttribute("lists", postRepository.findAllByCategoryOrderByIdDesc(category));
        return "posts/lists";
    }

    @PostMapping("/save")
    public String addPost(Post post, String tags, @RequestParam("file") MultipartFile file){

        if (!(file.isEmpty())){
            try {
                post.setPictureUrl(backendBase+"/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            post.setPictureUrl("http://placehold.it/800x600");
        }

        if (!(tags.isEmpty())){
            String [] tmp = tags.split(",");
            System.out.println(tmp);
            ArrayList<String> tmpTags = new ArrayList<>();
            for (String string: tmp){
                tmpTags.add(string);
            }
            System.out.println(tmpTags);
            post.setTags(tmpTags);
        }
        post.setStatus(false);
        postRepository.save(post);
        return "redirect:/admin/posts/lists";
    }

    @GetMapping("/update/{id}")
    public String updatForm(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        Post post = postRepository.getOne(id);
        model.addAttribute("post", post);
        model.addAttribute("connectedUser", user);
        return "posts/edit";
    }


    @PostMapping("/update")
    public String update(Post post, String tags, @RequestParam("file") MultipartFile file){
        if (!(file.isEmpty())){
            try {
                post.setPictureUrl(backendBase+"/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!(tags.isEmpty())){
            String [] tmp = tags.split(",");
            System.out.println(tmp);
            ArrayList<String> tmpTags = new ArrayList<>();
            for (String string: tmp){
                tmpTags.add(string);
            }
            System.out.println(tmpTags);
            post.setTags(tmpTags);
        }

        postRepository.save(post);

        return "redirect:/admin/posts/lists";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        postRepository.delete(postRepository.getOne(id));

        redirectAttributes.addFlashAttribute("success", "Vous avez supprimer avec success votre annonces");

        return "redirect:/admin/posts/lists";

    }
}
