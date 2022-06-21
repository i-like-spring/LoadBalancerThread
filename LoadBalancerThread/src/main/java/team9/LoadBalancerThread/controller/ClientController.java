package team9.LoadBalancerThread.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import team9.LoadBalancerThread.domain.Reservation;
import team9.LoadBalancerThread.service.LbService;

import java.time.LocalDateTime;

@Controller
public class ClientController {

    private LbService lbService;
    private boolean isReserved;
    public ClientController(LbService lbService) {
        this.lbService = lbService;
        isReserved = false;
    }

    @GetMapping("/")
    public String home(){ return "home"; }

    @PostMapping("/reservation") //예매라는 프로세스를 진행하는 것이니까 Post
    public String showReservationPage(){
        if(!isReserved) return "reservation/reservationForm";
        else return "redirect:/";
    }

    @PostMapping("/reservation/new")
    public String reserveTicket() throws Exception{
        isReserved = true;
        lbService.start();
        return "redirect:/";
    }



}
