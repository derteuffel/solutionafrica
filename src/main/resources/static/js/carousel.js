/*-----------------------
        Categories Slider
    ------------------------*/
$(".categories__slider").owlCarousel({
    loop: true,
    margin: 0,
    items: 3,
    dots: false,
    nav: true,
    navText: ["<span class='fa fa-angle-left'><span/>", "<span class='fa fa-angle-right'><span/>"],
    animateOut: 'fadeOut',
    animateIn: 'fadeIn',
    smartSpeed: 1200,
    autoHeight: false,
    autoplay: true,
    responsive: {

        0: {
            items: 1,
        },

        480: {
            items: 2,
        },

        768: {
            items: 3,
        }
    }
});

/*------------------
       Background Set
   --------------------*/
$('.set-bg').each(function () {
    var bg = $(this).data('setbg');
    $(this).css('background-image', 'url(' + bg + ')');
});