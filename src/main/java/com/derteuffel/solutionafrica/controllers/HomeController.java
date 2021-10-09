package com.derteuffel.solutionafrica.controllers;

import com.derteuffel.solutionafrica.entities.*;
import com.derteuffel.solutionafrica.enums.ProduitCategories;
import com.derteuffel.solutionafrica.helpers.MessageSending;
import com.derteuffel.solutionafrica.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private BoutiqueRepository boutiqueRepository;

    @GetMapping
    public String home(Model model){

        List<Post> posts = new ArrayList<>();
        List<Post> footerPosts = new ArrayList<>();


        //Body posts loaded
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 6) {
            posts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 6; i++) {
                posts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }


        //footer posts loaded
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        System.out.println(footerPosts);

        model.addAttribute("comment", new Comment());
        model.addAttribute("lists", posts);
        model.addAttribute("footerPosts", footerPosts);
        return "index";
    }

    @PostMapping("comments/save")
    public String commentSave(Comment comment){
        commentRepository.save(comment);
        return "redirect:/";
    }

    @GetMapping("blogs")
    public String blogs(Model model, HttpServletRequest request){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10; //default page size is 10

        List<Integer> commentSize = new ArrayList<>();
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        for (Post post : postRepository.findAll(PageRequest.of(page,size))){
            commentSize.add(commentRepository.findAllByPost_IdOrderByIdDesc(post.getId()).size());
        }

        model.addAttribute("lists",postRepository.findAll(PageRequest.of(page,size)));


        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        System.out.println(commentSize);
        model.addAttribute("commentSize", commentSize);
        model.addAttribute("footerPosts", footerPosts);
        return "blog";
    }

    @GetMapping("blogs/lists/{category}")
    public String blogsCategorie(Model model, HttpServletRequest request, @PathVariable String category){

        List<Integer> commentSize = new ArrayList<>();

        for (Post post : postRepository.findAllByCategoryOrderByIdDesc(category)){
            commentSize.add(commentRepository.findAllByPost_IdOrderByIdDesc(post.getId()).size());
        }
        model.addAttribute("lists",postRepository.findAllByCategoryOrderByIdDesc(category));


        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        System.out.println(commentSize);
        model.addAttribute("commentSize", commentSize);
        model.addAttribute("footerPosts", footerPosts);
        return "blogs/categories";
    }

    @GetMapping("blogs/details/{id}")
    public String getPost(@PathVariable Long id, Model model){
        Post post = postRepository.getOne(id);
        model.addAttribute("post", post);
        List<Comment> comments = new ArrayList<>();
        if (commentRepository.findAllByPost_IdOrderByIdDesc(post.getId()).size() < 10){
            comments.addAll(commentRepository.findAllByPost_IdOrderByIdAsc(post.getId()));
        }else {
            for (int i=0; i<10; i++){
                comments.add(commentRepository.findAllByPost_IdOrderByIdAsc(post.getId()).get(i));
            }
        }
        model.addAttribute("comments", comments);
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("commentSize", commentRepository.findAllByPost_IdOrderByIdDesc(post.getId()).size());
        model.addAttribute("comment", new Comment());
        model.addAttribute("footerPosts", footerPosts);
        return "blogs/blog-item";
    }

    @PostMapping("blogs/comments/{id}")
    public String addComment(Comment comment, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Post post = postRepository.getOne(id);
        comment.setPost(post);
        commentRepository.save(comment);
        return "redirect:/blogs/details/"+post.getId();
    }

    //////////
    //
    //E-Market implementations
    //
    //////////

    @GetMapping("ecommerces")
    public String ecommerce(Model model, HttpServletRequest request){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10;

        List<Boutique> boutiques = boutiqueRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        System.out.println(boutiques.size());

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("lists", produitRepository.findAllByOrderByIdDesc(PageRequest.of(page,size)));

        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("boutiques", boutiques);
        return "ecommerce/home";

    }

    @GetMapping("ecommerces/{id}")
    public String ecommerceBoutique(Model model, HttpServletRequest request, @PathVariable Long  id){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10;

        List<Boutique> boutiques = boutiqueRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        Boutique boutique = boutiqueRepository.getOne(id);
        System.out.println(boutiques.size());

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("lists", produitRepository.findAllByBoutique_Id(id,PageRequest.of(page,size)));
        model.addAttribute("boutique", boutique);
        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("boutiques", boutiques);
        return "ecommerce/boutique";

    }

    @GetMapping("ecommerces/{id}/{category}")
    public String ecommerceBoutiqueCategory(Model model, HttpServletRequest request, @PathVariable Long  id, ProduitCategories categorie){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10;

        List<Boutique> boutiques = boutiqueRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        Boutique boutique = boutiqueRepository.getOne(id);
        System.out.println(boutiques.size());

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("lists", produitRepository.findAllByCategoryAndBoutique_Id(categorie,id,PageRequest.of(page,size)));
        model.addAttribute("boutique", boutique);
        model.addAttribute("category",categorie);
        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("boutiques", boutiques);
        return "ecommerce/category";

    }

    @GetMapping("ecommerces/lists/{category}")
    public String ecommerceCategory(Model model, HttpServletRequest request, @PathVariable ProduitCategories category){

        int page = 0; //default page number is 0 (yes it is weird)
        int size = 10;

        List<Boutique> boutiques = boutiqueRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        System.out.println(boutiques.size());

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("lists", produitRepository.findAllByCategory(category,PageRequest.of(page,size)));
        model.addAttribute("category",category);
        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("boutiques", boutiques);
        return "ecommerce/categories";

    }

    @GetMapping("ecommerces/products/detail/{id}")
    public String ecommerceProductDetail(Model model, @PathVariable Long id){

        Produit produit = produitRepository.getOne(id);
        model.addAttribute("produit", produit);
        List<Post> footerPosts = new ArrayList<>();
        if (postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).size()<= 2) {
            footerPosts.addAll(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));

        }else {
            for (int i = 0; i < 2; i++) {
                footerPosts.add(postRepository.findAll(Sort.by(Sort.Direction.DESC,"id")).get(i));
            }
        }

        model.addAttribute("footerPosts", footerPosts);
        model.addAttribute("commande", new Commande());
        return "ecommerce/detail";

    }


    @PostMapping("/ecommerces/orders/{id}")
    public String makeCommande(@PathVariable Long id, @Valid Commande commande, String telephone,String quantity,
                               RedirectAttributes redirectAttributes){

        MessageSending messageSending = new MessageSending();
        Produit produit = produitRepository.getOne(id);
        commande.setOrderNum("N"+commandeRepository.findAll().size());
        commande.setDescription("Le numero : "+telephone+" a passe une commande: Quantite d'elements = "+Integer.parseInt(quantity)+", "+commande.getDescription()+", "+produit.getName()+", "+produit.getCategory());
        commandeRepository.save(commande);

        messageSending.sendMessage(telephone,commande.getDescription());

        redirectAttributes.addFlashAttribute("success", "Votre commande a ete transmise, vous serez contacter pour les modalites de paiement");
        return "redirect:/ecommerces/products/detail/"+produit.getId();
    }
}
