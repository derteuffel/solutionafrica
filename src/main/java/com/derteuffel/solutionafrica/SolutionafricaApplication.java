package com.derteuffel.solutionafrica;

import com.derteuffel.solutionafrica.helpers.UserDto;
import com.derteuffel.solutionafrica.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SolutionafricaApplication extends SpringBootServletInitializer implements CommandLineRunner{
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(SolutionafricaApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SolutionafricaApplication.class);
	}


    @Override
    public void run(String... args) throws Exception {
        /*UserDto userDto = new UserDto();
        userDto.setPassword("0000");
        userDto.setEmail("direction@solutionafrica.org");
        userDto.setPhone("+243 819 472 760");
        userDto.setRole("ROLE_ROOT");
        userDto.setUsername("admin");
        userService.save(userDto);*/
    }
}
