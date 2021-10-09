package com.derteuffel.solutionafrica.controllers;

import com.derteuffel.solutionafrica.entities.Boutique;
import com.derteuffel.solutionafrica.entities.Post;
import com.derteuffel.solutionafrica.entities.Produit;
import com.derteuffel.solutionafrica.entities.User;
import com.derteuffel.solutionafrica.enums.Roles;
import com.derteuffel.solutionafrica.repositories.BoutiqueRepository;
import com.derteuffel.solutionafrica.repositories.PostRepository;
import com.derteuffel.solutionafrica.repositories.ProduitRepository;
import com.derteuffel.solutionafrica.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class EcommerceController {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Value("${file.upload-dir}")
    private String location ;

    @Value("${backendServer}")
    private String backendBase ;

    @Autowired
    private BoutiqueRepository boutiqueRepository;


    @GetMapping("/boutiques/lists")
    public String getBoutiques(Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());

        List<Boutique> lists = new ArrayList<>();
        if (user.getRoles().contains(Roles.ROLE_ROOT.toString())){
            boutiqueRepository.findAll().forEach(lists::add);
        }else {
            boutiqueRepository.findAllByUser_Id(user.getId()).forEach(lists::add);
        }
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("lists", lists);
        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("connectedUser", user);
        model.addAttribute("boutique", new Boutique());

        return "ecommerce/boutiques/boutiques";
    }


    @PostMapping("/boutiques/save")
    public String saveBoutique(@Valid Boutique boutique, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
                               HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());

        if (!(file.isEmpty())){
            try {
                boutique.setProfile(backendBase+"/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            boutique.setProfile("http://placehold.jp/80x70.png");
        }

        boutique.setUser(user);
        boutiqueRepository.save(boutique);
        redirectAttributes.addFlashAttribute("success", "Vous avez ajouter une nouvelle boutique");
        return "redirect:/admin/boutiques/lists";
    }

    @GetMapping("/boutiques/detail/{id}")
    public String accueil(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        Boutique boutique = boutiqueRepository.getOne(id);
        System.out.println(principal.getName()+"ici");
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("connectedUser", user);
        model.addAttribute("lists", produitRepository.findAllByBoutique_Id(boutique.getId()));
        model.addAttribute("produit", new Produit());
        model.addAttribute("boutique", boutique);
        return "ecommerce/boutiques/produits/lists";
    }

    @GetMapping("/boutiques/update/{id}")
    public String updateBoutique(Model model, HttpServletRequest request, @PathVariable Long id){
        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(),principal.getName());
        Boutique boutique = boutiqueRepository.getOne(id);
        System.out.println(principal.getName()+"ici");
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("connectedUser", user);
        model.addAttribute("boutique", boutique);
        return "ecommerce/boutiques/edit";
    }

    @PostMapping("/boutiques/update")
    public String updateBoutique(@Valid Boutique boutique, @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes){
        Boutique existedBoutique = boutiqueRepository.getOne(boutique.getId());
        boutique.setUser(existedBoutique.getUser());
        if (!(file.isEmpty())){
            try {
                boutique.setProfile(backendBase+ "/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boutiqueRepository.save(boutique);

        redirectAttributes.addFlashAttribute("success", "Vous avez mis a jour votre boutique : "+ boutique.getName());
        return "redirect:/admin/boutiques/lists";

    }

    @GetMapping("/boutiques/delete/{id}")
    public String deleteBoutiques(@PathVariable Long id, RedirectAttributes redirectAttributes){

        Boutique boutique = produitRepository.getOne(id).getBoutique();
        produitRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","Vous avez supprimer un boutique");
        return "redirect:/admin/boutiques/lists";
    }

    @PostMapping("/produits/save/{id}")
    public String saveProduit(@Valid Produit produit, String price, @RequestParam("file")MultipartFile file,
                              RedirectAttributes redirectAttributes, @PathVariable Long id){

        Boutique boutique = boutiqueRepository.getOne(id);

        if (!(file.isEmpty())){
            try {
                produit.setPictureUrl(backendBase+ "/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            produit.setPictureUrl("http://placehold.jp/400x300.png");
        }

        if (!(price.isEmpty())){
            produit.setPrice(Double.parseDouble(price));
        }else {
          redirectAttributes.addFlashAttribute("error","Vous n'avez pas entrer la valeur du prix de votre produit");
          return "redirect:/admin/boutiques/detail/"+boutique.getId();
        }

        produit.setBoutique(boutique);
        produitRepository.save(produit);
        redirectAttributes.addFlashAttribute("success","Vous avez enregistrer avec succes votre produit");
        return "redirect:/admin/boutiques/detail/"+boutique.getId();
    }

    @GetMapping("/produits/update/{id}")
    public String updateProduit(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        User user = userService.findByUsernameOrEmail(principal.getName(), principal.getName());
        Produit produit = produitRepository.getOne(id);
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("produit",produit);
        model.addAttribute("connectedUser", user);
        return "ecommerce/boutiques/produits/edit";
    }


    @PostMapping("/produits/update")
    public String updateProduit(@Valid Produit produit, String montant, @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes){
        Boutique boutique = boutiqueRepository.getOne(produitRepository.getOne(produit.getId()).getId());
        if (!(file.isEmpty())){
            try {
                produit.setPictureUrl(backendBase+"/downloadFile/"+file.getOriginalFilename());
                Path path = Paths.get(location+ "/"+file.getOriginalFilename());
                byte[] bytes = file.getBytes();
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!(montant.isEmpty())){
            produit.setPrice(Double.parseDouble(montant));
        }else {
            produit.setPrice(produit.getPrice());
        }

        produitRepository.save(produit);

        redirectAttributes.addFlashAttribute("success", "Vous avez mis a jour ce produit : "+ produit.getName());
        return "redirect:/admin/boutiques/detail/"+boutique.getId();

    }

    @GetMapping("/produits/delete/{id}")
    public String deleteProduit(@PathVariable Long id, RedirectAttributes redirectAttributes){

        Boutique boutique = produitRepository.getOne(id).getBoutique();
        produitRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","Vous avez supprimer un produit");
        return "redirect:/admin/boutiques/detail/"+boutique.getId();
    }
}
