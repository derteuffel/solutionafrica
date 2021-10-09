package com.derteuffel.solutionafrica.controllers;

import com.derteuffel.solutionafrica.entities.Photo;
import com.derteuffel.solutionafrica.entities.User;
import com.derteuffel.solutionafrica.enums.PhotoCategories;
import com.derteuffel.solutionafrica.enums.Roles;
import com.derteuffel.solutionafrica.repositories.PhotoRepository;
import com.derteuffel.solutionafrica.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Controller
public class ImprimerieController {

    @Autowired
    private PhotoRepository photoRepository;
    @Value("${file.upload-dir}")
    private String location ;
    @Value("${backendServer}")
    private String backendBase ;


    @Autowired
    private UserService userService;

    ArrayList<String> supports = new ArrayList<>(Arrays.asList(
            "Papier couche",
            "Papier duplicataire",
            "Papier dore",
            "Polo",
            "Lacoste",
            "Gilet",
            "Chasuble",
            "Chemise",
            "Bache",
            "Vinyl",
            "Papier photo",
            "Farde chemise",
            "Stylo",
            "Tasse",
            "Tasse magique",
            "Papier bristole"
    ));



    @GetMapping("/ecommerces/imprimeries/lists")
    public String getAll(Model model, HttpServletRequest request){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10;

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        Page<Photo> lists = photoRepository.findAllByTypeAndStatusOrderByIdDesc(PageRequest.of(page,size),"Imprimerie",true);
        model.addAttribute("categories",supports);
        model.addAttribute("lists",lists);
        return "imprimeries/home";
    }

    @GetMapping("/ecommerces/imprimeries/lists/{category}")
    public String getAllByCategories(Model model, HttpServletRequest request, String category){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10;

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        Page<Photo> lists = photoRepository.findAllByCategoryAndTypeAndStatusOrderByIdDesc(PageRequest.of(page,size), category,"Imprimerie",true);
        model.addAttribute("lists",lists);
        model.addAttribute("categories",supports);
        model.addAttribute("category", category.toString());
        return "imprimeries/categories";
    }


    @GetMapping("/admin/imprimeries/lists")
    public String getAll(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes){

        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        System.out.println(user.getUsername());
        if (user.getStatus() == false){
            redirectAttributes.addFlashAttribute("error", "Votre compte est desactiver veillez contacte l'administrateur de ce site");
            return "redirect:/admin/login";
        }

        if (user.getRoles().contains(Roles.ROLE_ROOT)||user.getRoles().contains(Roles.ROLE_ADMINISTRATOR)){
            model.addAttribute("lists", photoRepository.findAllByType("Imprimerie"));
        }else {
            model.addAttribute("lists", photoRepository.findAllByTypeAndUser_Id("Imprimerie",user.getId()));
        }
        model.addAttribute("categories", supports);
        model.addAttribute("connectedUser", user);
        model.addAttribute("photo", new Photo());

        return "imprimeries/lists";
    }



    @PostMapping("/admin/imprimeries/save")
    public String addPost(Photo photo, @RequestParam("file") MultipartFile file, HttpServletRequest request,
                          RedirectAttributes redirectAttributes){

        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        if (!(file.isEmpty())){
            try {
                photo.setPictureUrl(backendBase+ "/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            photo.setPictureUrl("http://placehold.it/600x400");
        }
        photo.setAddedDate(new Date());
        photo.setUser(user);
        photo.setType("Imprimerie");
        photo.setStatus(false);

        photoRepository.save(photo);
        redirectAttributes.addFlashAttribute("success","Vous avez enregistrer avec success votre image");
        return "redirect:/admin/imprimeries/lists";
    }

    @GetMapping("/admin/imprimeries/update/{id}")
    public String updatForm(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        Photo photo = photoRepository.getOne(id);
        model.addAttribute("photo", photo);
        model.addAttribute("categories", supports);
        model.addAttribute("connectedUser", user);
        return "imprimeries/edit";
    }

    @GetMapping("/admin/imprimeries/activate/{id}")
    public String activateItem(@PathVariable Long id, RedirectAttributes redirectAttributes ){
        Photo photo = photoRepository.getOne(id);
        if (photo.getStatus() != null && photo.getStatus() == true){
            photo.setStatus(false);
            redirectAttributes.addFlashAttribute("success","Vous venez de desactiver un article, celui si ne sera plus visible par vos visiteurs ");
        }else {
            photo.setStatus(true);
            redirectAttributes.addFlashAttribute("success","Vous venez d'activer un article qui sera dorenavant visible per vos visiteurs");
        }
        photoRepository.save(photo);

        return "redirect:/admin/imprimeries/lists";

    }


    @PostMapping("/admin/imprimeries/update/{id}")
    public String update(Photo photo, @RequestParam("file") MultipartFile file, @PathVariable Long id){

        Photo existedPhoto = photoRepository.getOne(id);
        existedPhoto.setCategory(photo.getCategory());
        existedPhoto.setReleaseDate(photo.getReleaseDate());
        existedPhoto.setTitle(photo.getTitle());
        if (!(file.isEmpty())){
            try {
                photo.setPictureUrl(backendBase+ "/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        photoRepository.save(photo);

        return "redirect:/admin/imprimeries/lists";
    }

    @GetMapping("/admin/imprimeries/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        photoRepository.delete(photoRepository.getOne(id));

        redirectAttributes.addFlashAttribute("success", "Vous avez supprimer avec success votre annonces");

        return "redirect:/admin/imprimeries/lists";

    }
}
